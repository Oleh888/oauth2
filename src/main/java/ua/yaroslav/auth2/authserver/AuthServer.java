package ua.yaroslav.auth2.authserver;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ua.yaroslav.auth2.authserver.dto.AuthRequest;
import ua.yaroslav.auth2.authserver.dto.TokenRequest;
import ua.yaroslav.auth2.authserver.json.JSONUtil;
import ua.yaroslav.auth2.authserver.json.entity.AuthCode;
import ua.yaroslav.auth2.authserver.json.entity.TokenAccess;
import ua.yaroslav.auth2.authserver.json.entity.TokenRefresh;
import ua.yaroslav.auth2.store.InMemoryStore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class AuthServer {
    private final String CLIENT_ID = "client";
    private final String CLIENT_SECRET = "secret";
    private final String RESPONSE_TYPE = "code";
    private final JSONUtil jSONUtil;
    private final InMemoryStore store;

    public AuthServer(JSONUtil jSONUtil, InMemoryStore store) {
        this.jSONUtil = jSONUtil;
        this.store = new InMemoryStore();
    }

    @PostMapping(value = {"/auth"}, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void getCode(AuthRequest authRequest, HttpServletResponse response) throws IOException {
        System.out.println("\n--------------------get-code-invocation---------------------");
        System.out.println("client_id: \n\t" + authRequest.getClientID());
        System.out.println("response_type: \n\t" + authRequest.getResponseType());
        System.out.println("username: \n\t" + authRequest.getUsername());
        System.out.println("password: \n\t" + authRequest.getPassword());
        System.out.println("scope: \n\t" + "[" + authRequest.getScope() + "]\n");

        if (authRequest.getClientID().equals(CLIENT_ID)) {
            if (authRequest.getResponseType().equals(RESPONSE_TYPE)) {
                AuthCode code = jSONUtil.getCode(authRequest);
                store.addCode(code);
                String url = "https://developers.google.com/oauthplayground?" +
                        "code=" + jSONUtil.encodeObject(code) + "&" +
                        "state=markOne";
                response.sendRedirect(url);
            }
        }
    }

    @PostMapping("/token")
    @ResponseBody
    public String getToken(TokenRequest tokenRequest, HttpServletRequest request) throws IOException {
        System.out.println("tokenRequest.getGrantType(): " + tokenRequest.getGrantType());
        switch (tokenRequest.getGrantType()) {
            case "authorization_code": {
                synchronized (this){
                    System.out.println("\n--------------------get-token-invocation[GT:" + tokenRequest.getGrantType() + "]--------------------");
                    System.out.println("client_id: \n\t" + tokenRequest.getClientID());
                    System.out.println("client_secret: \n\t" + tokenRequest.getClientSecret());
                    System.out.println("grant_type: \n\t" + tokenRequest.getGrantType());
                    System.out.println("code: \n\t" + tokenRequest.getCode());
                    System.out.println("scope: \n\t" + "[" + tokenRequest.getScope() + "]\n");
                }
                AuthCode authCode = jSONUtil.readCodeFromB64(tokenRequest.getCode());
                TokenAccess access = jSONUtil.getAccessToken(authCode.getClientID(), authCode.getUsername(), tokenRequest.getScope());
                TokenRefresh refresh = jSONUtil.getRefreshToken(authCode.getClientID(), authCode.getUsername(), access.getTokenID());
                store.addToken(access);

                synchronized (this){
                    System.out.println("Refresh token as string after decode [AC] [" + refresh.getClass().getSimpleName() + "]:");
                    System.out.println(jSONUtil.objectToString(refresh));
                    System.out.println("Access token as string after decode [AC] [" + access.getClass().getSimpleName() + "]:");
                    System.out.println(jSONUtil.objectToString(access));
                }

                return "{\n" +
                        "token_type: \"" + access.getType() +"\",\n" +
                        "access_token: \"" + jSONUtil.encodeObject(access) + "\",\n" +
                        "refresh_token: \"" + jSONUtil.encodeObject(refresh) + "\",\n" +
                        "expires_in: " + access.getExpiresIn() +"\n" +
                        "}";
            }
            case "refresh_token": {
                TokenRefresh refresh = jSONUtil.readRefreshTokenFromB64(tokenRequest.getRefreshToken());
                String s = jSONUtil.objectToString(refresh);
                System.out.println("Refresh token as string after decode [RT] [" + refresh.getClass().getSimpleName() + "]:");
                System.out.println("\t" + s);

                synchronized (this){
                    System.out.println("\n============================");
                    for (TokenAccess a: store.getTokens())
                        System.out.println(a.getTokenID());
                    System.out.println("===============" + store.getTokens().size() + "==============\n");
                }

                TokenAccess access = jSONUtil.getAccessToken(
                        store.getTokenByID(refresh.getAccessTokenID()).getClientID(),
                        store.getTokenByID(refresh.getAccessTokenID()).getUsername(),
                        tokenRequest.getScope());
                access.setTokenID(store.getTokens().size());
                store.addToken(access);
                return "{\n" +
                        "token_type: \"" + access.getType() +"\",\n" +
                        "access_token: \"" + jSONUtil.encodeObject(access) + "\",\n" +
                        "expires_in: " + access.getExpiresIn() +"\n" +
                        "}";
            }
            default: {
                return "{\"error\": \"invalid_grant_type\"}";
            }
        }
    }

    @GetMapping("/")
    @ResponseBody
    public String getHome() {
        return "<h3>Hell Yeah</h3>";
    }

    @GetMapping("/auth")
    public String getLogin() {
        return "login";
    }
}