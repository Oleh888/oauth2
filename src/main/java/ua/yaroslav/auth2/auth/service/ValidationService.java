package ua.yaroslav.auth2.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ua.yaroslav.auth2.auth.dto.AuthRequestDto;
import ua.yaroslav.auth2.auth.dto.LoginRequestDto;
import ua.yaroslav.auth2.auth.dto.TokenRequestDto;
import ua.yaroslav.auth2.auth.entity.AccessToken;
import ua.yaroslav.auth2.auth.exception.ErrorType;
import ua.yaroslav.auth2.auth.exception.LoginException;
import ua.yaroslav.auth2.auth.exception.Oauth2Exception;
import ua.yaroslav.auth2.auth.store.iface.ClientStore;

import javax.servlet.http.HttpServletRequest;

@Service
public class ValidationService {
    private final Logger logger = LoggerFactory.getLogger(ValidationService.class);
    private final ClientStore store;
    private final JSONUtil util;

    public ValidationService(ClientStore store, JSONUtil util) {
        this.store = store;
        this.util = util;
    }

    public void validate(LoginRequestDto loginRequest) throws LoginException {
        if (store.checkClient(loginRequest.getClientID()))
            return;
        throw new LoginException("Invalid Client ID");
    }

    public void validate(AuthRequestDto authRequest) throws Oauth2Exception {
        if (store.checkClient(authRequest.getClientID()) && authRequest.getResponseType().equals("code"))
            return;
        throw new Oauth2Exception(ErrorType.invalid_request, "Invalid Client Authentication Code");

    }

    public void validate(TokenRequestDto tokenRequest)
            throws Oauth2Exception {
        if (tokenRequest.getGrantType() == null)
            throw new Oauth2Exception(ErrorType.invalid_request, "Invalid Grant Type");
        if (!store.checkClient(tokenRequest.getClientID()))
            throw new Oauth2Exception(ErrorType.invalid_request, "Invalid Client ID");
        if (store.checkClient(tokenRequest.getClientID(), tokenRequest.getClientSecret()))
            return;
        throw new Oauth2Exception(ErrorType.invalid_request, "Invalid Client Secret");

    }

    public void validate(HttpServletRequest request) throws Oauth2Exception {
        logger.info("Header: [" + request.getHeader("Authorization") + "]");

        if (request.getHeader("Authorization") != null) {
            String header = request.getHeader("Authorization");
            header = header.substring(7, header.length());

            AccessToken accessToken;
            try {
                accessToken = util.readTokenFromB64(header);
                logger.info(accessToken.toString());
            } catch (Exception e) {
                throw new Oauth2Exception(ErrorType.server_error, "Access Token Base64 Decode Exception");
            }

            logger.info("Access Token (decoded) ->");
            logger.info(util.objectToString(accessToken));

            if (accessToken.getTime() < System.currentTimeMillis())
                throw new Oauth2Exception(ErrorType.access_denied, "Access Token Has Expired");
        } else throw new Oauth2Exception(ErrorType.access_denied, "Invalid Access Token");
    }
}