package ua.yaroslav.auth2.datastore;
import org.springframework.stereotype.Component;
import ua.yaroslav.auth2.authserver.jwt.entity.JWTAuthCode;
import ua.yaroslav.auth2.authserver.jwt.entity.JWTToken;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@Component
public class Database {
    private ArrayList<JWTToken> tokens;
    private ArrayList<JWTAuthCode> codes;

    public Database() {
        this.tokens = new ArrayList<>();
        this.codes = new ArrayList<>();
    }

    public void addToken(JWTToken token){
        this.tokens.add(token);
    }

    public void addCode(JWTAuthCode code){
        this.codes.add(code);
    }

    public boolean isTokenValid(JWTToken token){
        return this.tokens.contains(token);
    }

    public boolean isCodeValid(JWTAuthCode code){
        return this.codes.contains(code);
    }

    public List<JWTAuthCode> getCodes(){
        return codes;
    }
}