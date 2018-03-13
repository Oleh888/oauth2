package ua.yaroslav.auth2.auth.service.implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ua.yaroslav.auth2.auth.service.ValidationService;
import ua.yaroslav.auth2.auth.store.ClientStore;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;

@Service
public class ValidationServiceImpl implements ValidationService {
    private final Logger logger = LoggerFactory.getLogger(ValidationServiceImpl.class);
    private final ClientStore store;
    private final ObjectMapper mapper;

    public ValidationServiceImpl(ClientStore store, ObjectMapper mapper) {
        this.store = store;
        this.mapper = mapper;
    }

    @Override
    public void validate(LoginRequestDto loginRequest) throws LoginException {
        if (store.checkClient(loginRequest.getClientID()))
            return;
        throw new LoginException("Invalid Client ID");
    }

    @Override
    public void validate(AuthRequestDto authRequest) throws Oauth2Exception {
        if (store.checkClient(authRequest.getClientID()) && authRequest.getResponseType().equals("code"))
            return;
        throw new Oauth2Exception(ErrorType.invalid_request, "Invalid Client Authentication Code");

    }

    @Override
    public void validate(TokenRequestDto tokenRequest) throws Oauth2Exception {
        if (tokenRequest.getGrantType() == null)
            throw new Oauth2Exception(ErrorType.invalid_request, "Invalid Grant Type");
        if (!store.checkClient(tokenRequest.getClientID()))
            throw new Oauth2Exception(ErrorType.invalid_request, "Invalid Client ID");
        if (store.checkClient(tokenRequest.getClientID(), tokenRequest.getClientSecret()))
            return;
        throw new Oauth2Exception(ErrorType.invalid_request, "Invalid Client Secret");

    }

    @Override
    public void validate(HttpServletRequest request) throws Oauth2Exception {
        logger.info("Header: [" + request.getHeader("Authorization") + "]");

        if (request.getHeader("Authorization") != null) {
            String header = request.getHeader("Authorization");
            header = header.substring(7, header.length());

            AccessToken accessToken;
            try {
                accessToken = mapper.readValue(new String(Base64.getDecoder().decode(header.getBytes())), AccessToken.class);
                logger.info(accessToken.toString());
            } catch (Exception e) {
                throw new Oauth2Exception(ErrorType.server_error, "Access Token Base64 Decode Exception");
            }

            logger.info("Access Token (decoded) ->");
            logger.info(accessToken.toString());

            if (accessToken.getTime() < System.currentTimeMillis())
                throw new Oauth2Exception(ErrorType.access_denied, "Access Token Has Expired");
        } else throw new Oauth2Exception(ErrorType.access_denied, "Invalid Access Token");
    }
}