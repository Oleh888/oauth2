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
import ua.yaroslav.auth2.store.iface.ClientStore;

@Component
public class Validator {
    private final ClientStore store;
    private final Logger logger = LoggerFactory.getLogger(Validator.class);

    public Validator(ClientStore store) {
        this.store = store;
    }

    public void validate(LoginRequestDto loginRequest) throws InvalidClientIDException {
        if (store.checkClient(loginRequest.getClientID()))
            return;
        throw new InvalidClientIDException();
    }

    public void validate(AuthRequestDto authRequest) throws InvalidClientAuthCodeException {
        if (store.checkClient(authRequest.getClientID()) && authRequest.getResponseType().equals("code"))
            return;
        throw new InvalidClientAuthCodeException();

    }

    public void validate(TokenRequestDto tokenRequest) throws InvalidClientSecretException {
        if (store.checkClient(tokenRequest.getClientID(), tokenRequest.getClientSecret()))
            return;
        throw new InvalidClientSecretException();

    }
}