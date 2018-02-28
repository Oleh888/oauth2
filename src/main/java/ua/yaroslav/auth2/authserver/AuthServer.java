package ua.yaroslav.auth2.authserver;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ua.yaroslav.auth2.authserver.json.JSONUtil;
import ua.yaroslav.auth2.authserver.json.entity.AuthCode;
import ua.yaroslav.auth2.authserver.json.entity.TokenAccess;
import ua.yaroslav.auth2.authserver.json.entity.TokenRefresh;
import ua.yaroslav.auth2.store.InMemoryStore;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class AuthServer {
    private final String CLIENT_ID = "client";
    private final String CLIENT_SECRET = "secret";
    private final String RESPONSE_TYPE = "code";
    private final JSONUtil jSONUtil;
    private final InMemoryStore inMemoryStore;

    public AuthServer(JSONUtil jSONUtil, InMemoryStore inMemoryStore) {
        this.jSONUtil = jSONUtil;
        this.inMemoryStore = new InMemoryStore();
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
                inMemoryStore.addCode(code);
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
                           @RequestParam(value = "code") String code,
                           @RequestParam(value = "scope") String scope,
                           @RequestParam(value = "refresh_token", required = false) String refreshToken) throws IOException {
        System.out.println("\n--------------------get-token-invocation--------------------");
        System.out.println("client_id: \n\t" + client_id);
        System.out.println("client_secret: \n\t" + client_secret);
        System.out.println("grant_type: \n\t" + grant_type);
        System.out.println("code: \n\t" + code);
        System.out.println("scope: \n\t" + "[" + scope + "]\n");

        switch (grant_type) {
            case "authorization_code": {
                AuthCode authCode = jSONUtil.readCodeFromB64(code);
                if (inMemoryStore.isCodeValid(authCode)) {
                    TokenAccess access = jSONUtil.getAccessToken(authCode.getClientID(), authCode.getUsername(), scope);
                    TokenRefresh refresh = jSONUtil.getRefreshToken(authCode.getClientID(), authCode.getUsername(), access.hashCode());

                    return "{\n" +
                            "token_type: \"" + access.getType() +"\",\n" +
                            "access_token: \"" + jSONUtil.encodeObject(access) + "\",\n" +
                            "refresh_token: \"" + jSONUtil.encodeObject(refresh) + "\",\n" +
                            "expires_in: " + access.getExpiresIn() +"\n" +
                            "}";
                }
            }
            case "refresh_token": {
                System.out.println("Refresh Token: " + refreshToken);
                TokenRefresh refresh = jSONUtil.readRefreshTokenFromB64(refreshToken);
                System.out.println("Mapped Object:");
                System.out.println(refresh);

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