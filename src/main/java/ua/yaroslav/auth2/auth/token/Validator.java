package ua.yaroslav.auth2.auth.token;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ua.yaroslav.auth2.auth.dto.AuthRequestDto;
import ua.yaroslav.auth2.auth.dto.LoginRequestDto;
import ua.yaroslav.auth2.auth.dto.TokenRequestDto;
import ua.yaroslav.auth2.auth.exception.InvalidClientAuthCodeException;
import ua.yaroslav.auth2.auth.exception.InvalidClientIDException;
import ua.yaroslav.auth2.auth.exception.InvalidClientSecretException;
import ua.yaroslav.auth2.store.InMemoryStore;

@Component
public class Validator {
    private final InMemoryStore store;
    private final Logger logger = LoggerFactory.getLogger(Validator.class);

    public Validator(InMemoryStore store) {
        this.store = store;
    }

    public boolean validate(LoginRequestDto loginRequest) throws InvalidClientIDException {
        if (store.checkClient(loginRequest.getClientID()))
            return true;
        throw new InvalidClientIDException();
    }

    public boolean validate(AuthRequestDto authRequest) throws InvalidClientAuthCodeException {
        if (store.checkClient(authRequest.getClientID()) && authRequest.getResponseType().equals("code"))
            return true;
        throw new InvalidClientAuthCodeException();

    }

    public boolean validate(TokenRequestDto tokenRequest) throws InvalidClientSecretException {
        if(store.checkClient(tokenRequest.getClientID(), tokenRequest.getClientSecret()))
            return true;
        throw new InvalidClientSecretException();

    }
}