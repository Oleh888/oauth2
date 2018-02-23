package ua.yaroslav.auth2.authserver;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.yaroslav.auth2.datastore.Database;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class AuthServer {
    private final Database database;
    private final String CLIENT_ID = "client";
    private final String CLIENT_SECRET = "secret";

    public AuthServer(Database database) {
        this.database = database;
    }

    @RequestMapping("/auth")
    public void authorize(HttpServletRequest request,
                          HttpServletResponse response,
                          @RequestParam(value="client_id") String client_id,
                          @RequestParam(value="redirect_uri") String redirect_uri,
                          @RequestParam(value="response_type") String response_type) throws IOException, ServletException {
        System.out.println(client_id);
        System.out.println(redirect_uri);
        System.out.println(response_type);

    }

    @RequestMapping("/")
    public String getHome(){
        return "<h3>Hell Yeah Server</h3>";
    }
}