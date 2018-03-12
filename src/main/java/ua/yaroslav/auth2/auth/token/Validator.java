package ua.yaroslav.auth2.auth.token;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ua.yaroslav.auth2.auth.dto.AuthRequestDto;
import ua.yaroslav.auth2.auth.dto.LoginRequestDto;
import ua.yaroslav.auth2.auth.dto.TokenRequestDto;
import ua.yaroslav.auth2.auth.exception.ErrorType;
import ua.yaroslav.auth2.auth.exception.Oauth2Exception;
import ua.yaroslav.auth2.auth.json.JSONUtil;
import ua.yaroslav.auth2.entity.AccessToken;
import ua.yaroslav.auth2.store.iface.ClientStore;

import javax.servlet.http.HttpServletRequest;

@Component
public class Validator {
    private final Logger logger = LoggerFactory.getLogger(Validator.class);
    private final ClientStore store;
    private final JSONUtil util;

    public Validator(ClientStore store, JSONUtil util) {
        this.store = store;
        this.util = util;
    }

    public void validate(LoginRequestDto loginRequest) throws Oauth2Exception {
        if (store.checkClient(loginRequest.getClientID()))
            return;
        throw new Oauth2Exception(ErrorType.invalid_request, "Invalide Client ID");
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
            throw new Oauth2Exception(ErrorType.invalid_request, "Invalide Client ID");
        if (store.checkClient(tokenRequest.getClientID(), tokenRequest.getClientSecret()))
            return;
        throw new Oauth2Exception(ErrorType.invalid_request, "Invalide Client Secret");

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