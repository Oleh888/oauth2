package ua.yaroslav.auth2.datastore;
import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.Set;

@Component
public class Database {
    private Set<String> authCodes = new HashSet<>();
    private Set<String> tokens = new HashSet<>();
    private Set<User> users = new HashSet<>();

    public Database(){this.tokens.add("SlAV32hkKG");}

    public void addAuthCode(String authCode) {
        authCodes.add(authCode);
    }

    public boolean isValidAuthCode(String authCode) {
        return authCodes.contains(authCode);
    }

    public void addToken(String token) {
        tokens.add(token);
    }

    public boolean isValidToken(String token) {
        return tokens.contains(token);
    }

    public void addUser(User user) {users.add(user); }

    public boolean checkUser(String login, String pass){
        for (User u: users){
            return u.getLogin().equals(login) && u.getPass().equals(pass);
        }
        return false;
    }
}