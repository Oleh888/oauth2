package ua.yaroslav.auth2.datastore;

import org.springframework.stereotype.Component;
import ua.yaroslav.auth2.authserver.jwt.entity.JWTAuthCode;
import ua.yaroslav.auth2.authserver.jwt.entity.JWTToken;

import java.util.ArrayList;

@Component
public class Database {
    private final ArrayList<JWTToken> tokens;
    private final ArrayList<JWTAuthCode> codes;

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

    public ArrayList<JWTAuthCode> getCodes(){
        return codes;
    }
    public ArrayList<JWTToken> getTokens(){ return tokens; }
}