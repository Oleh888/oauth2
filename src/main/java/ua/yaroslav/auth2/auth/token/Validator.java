package ua.yaroslav.auth2.auth.token;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ua.yaroslav.auth2.auth.dto.AuthRequestDto;
import ua.yaroslav.auth2.auth.dto.LoginRequestDto;
import ua.yaroslav.auth2.auth.dto.TokenRequestDto;
import ua.yaroslav.auth2.auth.exception.*;
import ua.yaroslav.auth2.auth.json.JSONUtil;
import ua.yaroslav.auth2.entity.AccessToken;
import ua.yaroslav.auth2.store.iface.ClientStore;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class Validator {
    private final Logger logger = LoggerFactory.getLogger(Validator.class);
    private final ClientStore store;
    private final JSONUtil util;

    public Validator(ClientStore store, JSONUtil util) {
        this.store = store;
        this.util = util;
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

    public void validate(TokenRequestDto tokenRequest) throws InvalidClientSecretException, InvalidClientIDException {
        if (!store.checkClient(tokenRequest.getClientID()))
            throw new InvalidClientIDException();
        if (store.checkClient(tokenRequest.getClientID(), tokenRequest.getClientSecret()))
            return;
        throw new InvalidClientSecretException();

    }

    public void validate(HttpServletRequest request) throws IOException,
            AccessTokenHasExpiredException, AccessTokenInvalidException {
        logger.info("Header: [" + request.getHeader("Authorization") + "]");

        if (request.getHeader("Authorization") != null) {
            String header = request.getHeader("Authorization");
            header = header.substring(7, header.length());
            AccessToken accessToken = util.readTokenFromB64(header);
            logger.info("Access Token (decoded) ->");
            logger.info(util.objectToString(accessToken));

            if (accessToken.getTime() < System.currentTimeMillis())
                throw new AccessTokenHasExpiredException();
        } else throw new AccessTokenInvalidException();
    }
}