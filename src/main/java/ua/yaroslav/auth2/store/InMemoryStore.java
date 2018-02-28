package ua.yaroslav.auth2.store;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ua.yaroslav.auth2.authserver.json.entity.AuthCode;
import ua.yaroslav.auth2.authserver.json.entity.TokenAccess;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class InMemoryStore {
    private final CopyOnWriteArrayList<TokenAccess> tokens;
    private final CopyOnWriteArrayList<AuthCode> codes;


    public InMemoryStore() {
        this.tokens = new CopyOnWriteArrayList<>();
        this.codes = new CopyOnWriteArrayList<>();
    }


    public void addToken(TokenAccess token){
        synchronized (this){
            this.tokens.add(token);
        }
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

    public CopyOnWriteArrayList<AuthCode> getCodes(){
        return codes;
    }

    public CopyOnWriteArrayList<TokenAccess> getTokens(){ return tokens; }
}