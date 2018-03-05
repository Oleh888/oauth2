package ua.yaroslav.auth2.store;

import org.springframework.stereotype.Repository;
import ua.yaroslav.auth2.auth.entity.AccessToken;
import ua.yaroslav.auth2.auth.entity.AuthCode;
import ua.yaroslav.auth2.auth.entity.Client;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class InMemoryStore {
    private final CopyOnWriteArrayList<AccessToken> tokens;
    private final CopyOnWriteArrayList<AuthCode> codes;
    private final HashMap<String, Client> clients;


    public InMemoryStore() {
        this.tokens = new CopyOnWriteArrayList<>();
        this.codes = new CopyOnWriteArrayList<>();
        this.clients = new HashMap<>();
    }


    public void addToken(AccessToken token) {
        this.tokens.add(token);
    }

    public void addCode(AuthCode code) {
        this.codes.add(code);
    }

    public boolean isTokenValid(AccessToken token) {
        return this.tokens.contains(token);
    }

    public boolean isCodeValid(AuthCode code) {
        return this.codes.contains(code);
    }

    public CopyOnWriteArrayList<AuthCode> getCodes() {
        return codes;
    }

    public CopyOnWriteArrayList<AccessToken> getTokens() {
        return tokens;
    }

    public void addClient(Client client) {
        this.clients.put(client.getName(), client);
    }

    public boolean checkUser(String name) {
        return this.clients.containsKey(name);
    }

    public boolean checkUser(String name, String secret) {
        return checkUser(name) && this.clients.get(name).getSecret().equals(secret);
    }

    @PostConstruct
    public void init(){
        this.clients.put("client", new Client("client","secret"));
    }
}