package ua.yaroslav.auth2.auth.token;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ua.yaroslav.auth2.auth.dto.AuthRequestDto;
import ua.yaroslav.auth2.auth.dto.LoginRequestDto;
import ua.yaroslav.auth2.auth.dto.TokenRequestDto;
import ua.yaroslav.auth2.store.InMemoryStore;

@Component
public class Validator {
    private final InMemoryStore store;
    private final Logger logger = LoggerFactory.getLogger(Validator.class);

    public Validator(InMemoryStore store) {
        this.store = store;
    }

    public boolean validate(LoginRequestDto loginRequest) {
        return store.checkClient(loginRequest.getClientID());
    }

    public boolean validate(AuthRequestDto authRequest) {
        return store.checkClient(authRequest.getClientID()) && authRequest.getResponseType().equals("code");
    }

    public boolean validate(TokenRequestDto tokenRequest) {
        return store.checkClient(tokenRequest.getClientID(), tokenRequest.getClientSecret());
    }
}