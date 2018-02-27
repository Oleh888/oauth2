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
        System.out.println("client_id: \n\t" + formData.getClientID());
        System.out.println("redirect_uri: \n\t" + formData.getRedirectURI());
        System.out.println("response_type: \n\t" + formData.getResponseType());
        System.out.println("username: \n\t" + formData.getUsername());
        System.out.println("password: \n\t" + formData.getPassword());
        System.out.println("scope: \n\t" + formData.getScope());

        if (formData.getClientID().equals(CLIENT_ID)) {
            if (formData.getResponseType().equals("code")) {
                String url = "https://developers.google.com/oauthplayground?code=" + jwtUtil.getCode(formData) + "&state=markOne";
                response.sendRedirect(url);
            }
        }
    }

    @PostMapping("/token")
    @ResponseBody
    public String getToken(@RequestParam(value = "client_id") String client_id,
                           @RequestParam(value = "client_secret") String client_secret,
                           @RequestParam(value = "grant_type") String grant_type,
                           @RequestParam(value = "code") String code) {
        System.out.println("\n--get token invocation--");
        System.out.println("client_id: \n\t" + client_id);
        System.out.println("client_secret: \n\t" + client_secret);
        System.out.println("grant_type: \n\t" + grant_type);
        System.out.println("code: \n\t" + code);

        switch (grant_type) {
            case "authorization_code": {
                if (database.isValidAuthCode(code)) {
                    String encoded = jwtUtil.decodeAC(code);
                    System.out.println("Decoded data:");
                    System.out.println(encoded);
                    database.addToken("SlAV32hkKG");
                    return "{\n" +
                            "  token_type: \"bearer\",\n" +
                            "  access_token: \"SlAV32hkKG\",\n" +
                            "  refresh_token: \"8xLOxBtZp8\",\n" +
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