package ua.yaroslav.auth2.store;

import org.springframework.stereotype.Component;
import ua.yaroslav.auth2.authserver.json.entity.AuthCode;
import ua.yaroslav.auth2.authserver.json.entity.TokenAccess;

import java.util.ArrayList;

@Component
public class InMemoryStore {
    private final ArrayList<TokenAccess> tokens;
    private final ArrayList<AuthCode> codes;

    public InMemoryStore() {
        this.tokens = new ArrayList<>();
        this.codes = new ArrayList<>();
        this.tokens.add(new TokenAccess("client","user",9999,"grant_all","bearer", 9999));
        this.codes.add(new AuthCode("client","user", 9999));
    }

    public void addToken(TokenAccess token){
        this.tokens.add(token);
    }

    public void addCode(AuthCode code){
        this.codes.add(code);
    }

    public boolean isTokenValid(TokenAccess token){
        return this.tokens.contains(token);
    }

    public boolean isCodeValid(AuthCode code){
        return this.codes.contains(code);
    }

    public ArrayList<AuthCode> getCodes(){
        return codes;
    }
    public ArrayList<TokenAccess> getTokens(){ return tokens; }

    public TokenAccess getTokenByHash(int hash){
        for (TokenAccess ta: this.tokens){
            if (ta.hashCode() == hash) return ta;
        }
        return null;
    }
}