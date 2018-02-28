package ua.yaroslav.auth2.store;

import org.springframework.stereotype.Component;
import ua.yaroslav.auth2.authserver.jwt.entity.JWTAuthCode;
import ua.yaroslav.auth2.authserver.jwt.entity.JWTToken;

import java.util.ArrayList;

@Component
public class InMemoryStore {
    private final ArrayList<JWTToken> tokens;
    private final ArrayList<JWTAuthCode> codes;

    public InMemoryStore() {
        this.tokens = new ArrayList<>();
        this.codes = new ArrayList<>();
        this.tokens.add(new JWTToken("client","user",9999,"grant_all","bearer"));
        this.codes.add(new JWTAuthCode("client","user", 9999));
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