package ua.yaroslav.auth2.authserver;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
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
    public void getCode(FormData formData, HttpServletResponse response) throws IOException {
        System.out.println("\n--------------------get-code-invocation---------------------");
        System.out.println("client_id: \n\t" + formData.getClientID());
        System.out.println("response_type: \n\t" + formData.getResponseType());
        System.out.println("username: \n\t" + formData.getUsername());
        System.out.println("password: \n\t" + formData.getPassword());
        System.out.println("scope: \n\t" + "[" + formData.getScope() + "]\n");

        if (formData.getClientID().equals(CLIENT_ID)) {
            if (formData.getResponseType().equals(RESPONSE_TYPE)) {
                AuthCode code = jSONUtil.getCode(formData);
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
    public String getToken(@RequestParam(value = "client_id") String client_id,
                           @RequestParam(value = "client_secret") String client_secret,
                           @RequestParam(value = "grant_type") String grant_type,
                           @RequestParam(value = "code", required = false) String code,
                           @RequestParam(value = "scope", required = false) String scope,
                           @RequestParam(value = "refresh_token", required = false) String refreshToken,
                           HttpServletRequest request) throws IOException {
        synchronized (this){
            System.out.println("\n--------------------get-token-invocation[GT:" + grant_type + "]--------------------");
            System.out.println("client_id: \n\t" + client_id);
            System.out.println("client_secret: \n\t" + client_secret);
            System.out.println("grant_type: \n\t" + grant_type);
            System.out.println("code: \n\t" + code);
            System.out.println("scope: \n\t" + "[" + scope + "]\n");
        }

        switch (grant_type) {
            case "authorization_code": {
                AuthCode authCode = jSONUtil.readCodeFromB64(code);
//                if (store.isCodeValid(authCode)) {
//                    TokenAccess access = jSONUtil.getAccessToken(authCode.getClientID(), authCode.getUsername(), scope);
//                    TokenRefresh refresh = jSONUtil.getRefreshToken(authCode.getClientID(), authCode.getUsername(), access.getTokenID());
//
//                    String s = jSONUtil.objectToString(refresh);
//                    System.out.println("refresh token as string after decode [" + refresh.getClass().getSimpleName() + "]:");
//                    System.out.println("\t" + s);
//                    return "{\n" +
//                            "token_type: \"" + access.getType() +"\",\n" +
//                            "access_token: \"" + jSONUtil.encodeObject(access) + "\",\n" +
//                            "refresh_token: \"" + jSONUtil.encodeObject(refresh) + "\",\n" +
//                            "expires_in: " + access.getExpiresIn() +"\n" +
//                            "}";
//                }
//                break;
                TokenAccess access = jSONUtil.getAccessToken(authCode.getClientID(), authCode.getUsername(), scope);
                TokenRefresh refresh = jSONUtil.getRefreshToken(authCode.getClientID(), authCode.getUsername(), access.getTokenID());
                store.addToken(access);

                synchronized (this){
                    System.out.println("Refresh token as string after decode [AC] [" + refresh.getClass().getSimpleName() + "]:");
                    System.out.println("\t" + jSONUtil.objectToString(refresh));
                    System.out.println("Access token as string after decode [AC] [" + access.getClass().getSimpleName() + "]:");
                    System.out.println("\t" + jSONUtil.objectToString(access));
                }

                return "{\n" +
                        "token_type: \"" + access.getType() +"\",\n" +
                        "access_token: \"" + jSONUtil.encodeObject(access) + "\",\n" +
                        "refresh_token: \"" + jSONUtil.encodeObject(refresh) + "\",\n" +
                        "expires_in: " + access.getExpiresIn() +"\n" +
                        "}";
            }
            case "refresh_token": {
                TokenRefresh refresh = jSONUtil.readRefreshTokenFromB64(refreshToken);
                String s = jSONUtil.objectToString(refresh);
                System.out.println("Refresh token as string after decode [RT] [" + refresh.getClass().getSimpleName() + "]:");
                System.out.println("\t" + s);

                synchronized (this){
                    System.out.println("\n============================");
                    for (TokenAccess a: store.getTokens())
                        System.out.println(a.getTokenID());
                    System.out.println("=============================\n");
                }

                TokenAccess access = jSONUtil.getAccessToken(
                        store.getTokenByID(refresh.getAccessTokenID()).getClientID(),
                        store.getTokenByID(refresh.getAccessTokenID()).getUsername(),
                        scope);
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