package ua.yaroslav.auth2.authserver;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ua.yaroslav.auth2.authserver.jwt.JWTUtil;
import ua.yaroslav.auth2.authserver.jwt.entity.JWTAuthCode;
import ua.yaroslav.auth2.authserver.jwt.entity.JWTToken;
import ua.yaroslav.auth2.datastore.Database;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@Controller
public class AuthServer {
    private final String CLIENT_ID = "client";
    private final String CLIENT_SECRET = "secret";
    private final String RESPONSE_TYPE = "code";
    private final JWTUtil jwtUtil;
    private final Database database;

    public AuthServer(JWTUtil jwtUtil, Database database) {
        this.jwtUtil = jwtUtil;
        this.database = new Database();
    }

    @PostMapping(value = {"/auth"}, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void getCode(FormData formData, HttpServletResponse response) throws IOException {
        System.out.println("\n--------------------get-code-invocation---------------------");
        System.out.println("client_id: \n\t" + formData.getClientID());
        System.out.println("response_type: \n\t" + formData.getResponseType());
        System.out.println("username: \n\t" + formData.getUsername());
        System.out.println("password: \n\t" + formData.getPassword());
        System.out.println("scope: \n\t" + formData.getScope());

        if (formData.getClientID().equals(CLIENT_ID)) {
            if (formData.getResponseType().equals(RESPONSE_TYPE)) {
                JWTAuthCode code = jwtUtil.getCode(formData);
                database.addCode(code);
                String url = "https://developers.google.com/oauthplayground?" +
                        "code=" + jwtUtil.encodeObject(code) + "&" +
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
                           @RequestParam(value = "scope") String scope) throws IOException {
        System.out.println("\n--------------------get-token-invocation--------------------");
        System.out.println("client_id: \n\t" + client_id);
        System.out.println("client_secret: \n\t" + client_secret);
        System.out.println("grant_type: \n\t" + grant_type);
        System.out.println("code: \n\t" + code);
        System.out.println("scope: \n\t" + "[" + scope + "]");
        System.out.println();

        switch (grant_type) {
            case "authorization_code": {
                JWTAuthCode jwtAuthCode = jwtUtil.readCodeFromB64(code);
                if (database.isCodeValid(jwtAuthCode)) {
                    JWTToken token = jwtUtil.getToken(jwtAuthCode.getClientID(), jwtAuthCode.getUsername(), scope);
                    jwtUtil.encodeObject(token);
                }
//                if (database.isValidAuthCode(code)) {
//                    System.out.println("\nDecoded data:");
//                    System.out.println(jwtUtil.decodeAC(code));
//
//                    database.addToken("SlAV32hkKG");
//                    System.out.println("------------------------------------------------------------");
//                    System.out.println(jwtUtil.getToken(client_id,"username","read"));
//                    return "{\n" +
//                            "token_type: \"bearer\",\n" +
//                            "access_token: \"SlAV32hkKG\",\n" +
//                            "refresh_token: \"8xLOxBtZp8\",\n" +
//                            "expires_in: 3600\n" +
//                            "}";
//                }
                return "{\n" +
                            "token_type: \"bearer\",\n" +
                            "access_token: \"SlAV32hkKG\",\n" +
                            "refresh_token: \"8xLOxBtZp8\",\n" +
                            "expires_in: 3600\n" +
                            "}";
            }
            case "refresh_token": {
                System.out.println("RT");
            }
            default: {
                return "{\"error\": \"invalid_grant_type_maafaka\"}";
            }
        }
    }

    @GetMapping("/")
    @ResponseBody
    public String getHome() {
        return "<h3>Hell Yeah</h3>";
    }

    @GetMapping(value = {"/tokens"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ArrayList<JWTToken> getTokens(){
        return database.getTokens();
    }

    @GetMapping("/auth")
    public String getLogin() {
        return "login";
    }
}