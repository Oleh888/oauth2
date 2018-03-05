package ua.yaroslav.auth2.auth.token;

import org.springframework.stereotype.Component;
import ua.yaroslav.auth2.auth.dto.AuthRequestDto;
import ua.yaroslav.auth2.auth.dto.LoginRequestDto;
import ua.yaroslav.auth2.auth.dto.TokenRequestDto;
import ua.yaroslav.auth2.store.InMemoryStore;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class Validator {
    private final InMemoryStore store;

    public Validator(InMemoryStore store) {
        this.store = store;
    }

    public boolean validate(LoginRequestDto loginRequest) {
        try {
            System.out.println("Validation");
            HttpURLConnection connection = (HttpURLConnection) new URL(loginRequest.getRedirectURI()).openConnection();
            connection.setRequestMethod("HEAD");
            return store.checkUser(loginRequest.getClientID()) && connection.getResponseCode() == 200;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean validate(AuthRequestDto authRequest) {
        return store.checkUser(authRequest.getClientID()) && authRequest.getResponseType().equals("code");
    }

    public boolean validate(TokenRequestDto tokenRequest) {
        return store.checkUser(tokenRequest.getClientID(), tokenRequest.getClientSecret());
    }
}