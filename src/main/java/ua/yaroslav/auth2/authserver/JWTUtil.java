package ua.yaroslav.auth2.authserver;
import org.springframework.stereotype.Component;
import ua.yaroslav.auth2.datastore.Database;
import java.util.Base64;
import java.util.Date;

@Component
public class JWTUtil {
    private final Database database;
    public JWTUtil(Database database) { this.database = database; }

    public String getCode(FormData formData){
        String json = "{\n" +
                "  client_id: \"" + formData.getClientID() + "\",\n" +
                "  username: \"" + formData.getUsername() + "\",\n" +
                "  time: " + new Date().getTime()+ "\n" +
                "}";

        System.out.println(json);
        String authCode = Base64.getEncoder().encodeToString(json.getBytes());
        database.addAuthCode(authCode);
        return authCode;
    }

    public String decodeAC(String code) {
        return new String(Base64.getDecoder().decode(code));
    }
}