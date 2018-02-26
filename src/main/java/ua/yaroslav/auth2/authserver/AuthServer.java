package ua.yaroslav.auth2.authserver;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ua.yaroslav.auth2.datastore.Database;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class AuthServer {
    private final String CLIENT_ID = "client";
    private final String CLIENT_SECRET = "secret";
    private final Database database;
    private final JWTUtil jwtUtil;

    public AuthServer(Database database, JWTUtil jwtUtil) {
        this.database = database;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping(value = {"/auth"}, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void getCode(FormData formData, HttpServletResponse response) throws IOException {
        System.out.println("\n--get code invocation--");
        System.out.println("client_id: " + formData.getClient_id());
        System.out.println("redirect_uri: " + formData.getRedirect_uri());
        System.out.println("response_type: " + formData.getResponse_type());
        System.out.println("username: " + formData.getUsername());
        System.out.println("password: " + formData.getPassword());
        System.out.println("scope: " + formData.getScope());

        if (formData.getClient_id().equals(CLIENT_ID)) {
            if (formData.getResponse_type().equals("code")) {
                response.sendRedirect(formData.getRedirect_uri() + "?authorization_code=" + jwtUtil.getCode(formData));
            }
        }
    }

    @PostMapping("/token")
    @ResponseBody
    public String getToken(@RequestParam(value = "client_id") String client_id,
                           @RequestParam(value = "client_secret") String client_secret,
                           @RequestParam(value = "grant_type") String grant_type,
                           @RequestParam(value = "code") String code,
                           @RequestParam(value = "scope") String scope) {
        System.out.println("\n--get token invocation--");
        System.out.println("client_id: " + client_id);
        System.out.println("client_secret: " + client_secret);
        System.out.println("grand_type: " + grant_type);
        System.out.println("code: " + code);
        System.out.println("scope: " + scope);

        switch (grant_type) {
            case "authorization_code": {
                if (database.isValidAuthCode(code)) {
                    String encoded = jwtUtil.decodeAC(code);
                    System.out.println("Decoded data:");
                    System.out.println(encoded);
                    return "{\n" +
                            "  token_type: \"bearer\",\n" +
                            "  access_token: \"ACCESS_TOKEN\",\n" +
                            "  refresh_token: \"REFRESH_TOKEN\",\n" +
                            "  expires_in: 3600\n" +
                            "}";
                }
            }
            case "refresh_token": {

            }
            default: {
                return "{\"error\": \"invalid_grant_type\"}";
            }
        }
    }

    @RequestMapping("/")
    @ResponseBody
    public String getHome() {
        return "<h3>Hell Yeah</h3>";
    }

    @GetMapping("/auth")
    public String getLogin() {
        return "login";
    }
}