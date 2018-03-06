package ua.yaroslav.auth2.auth.token;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ua.yaroslav.auth2.auth.dto.AuthRequestDto;
import ua.yaroslav.auth2.auth.dto.TokenRequestDto;
import ua.yaroslav.auth2.auth.dto.TokenResponseDto;
import ua.yaroslav.auth2.auth.exception.InvalidClientAuthCodeException;
import ua.yaroslav.auth2.auth.exception.InvalidClientSecretException;
import ua.yaroslav.auth2.auth.json.JSONUtil;
import ua.yaroslav.auth2.entity.AccessToken;
import ua.yaroslav.auth2.entity.AuthCode;
import ua.yaroslav.auth2.entity.RefreshToken;
import ua.yaroslav.auth2.store.iface.CodeStore;
import ua.yaroslav.auth2.store.iface.TokenStore;

import java.io.IOException;

@Component
public class Generator {
    private final Validator validator;
    private final TokenStore tokenStore;
    private final CodeStore codeStore;
    private final JSONUtil util;
    private static final Logger logger = LoggerFactory.getLogger(Generator.class);


    public Generator(Validator validator, TokenStore tokenStore, CodeStore codeStore, JSONUtil util) {
        this.validator = validator;
        this.tokenStore = tokenStore;
        this.codeStore = codeStore;
        this.util = util;
    }


    public Validator getValidator() {
        return validator;
    }

    public String getURL(AuthRequestDto authRequest) throws InvalidClientAuthCodeException {
        validator.validate(authRequest);
        AuthCode code = util.getCode(authRequest);
        codeStore.saveCode(code);

        URLBuilder builder = new URLBuilder();
        return builder
                .setRedirect(authRequest.getRedirectURI())
                .setCode(util.encodeObject(code))
                .setState("awesome")
                .build();
    }

    public TokenResponseDto getTokensAsJSON(TokenRequestDto tokenRequest) throws IOException, InvalidClientSecretException {
        validator.validate(tokenRequest);

        AuthCode authCode = util.readCodeFromB64(tokenRequest.getCode());
        AccessToken access = util.getAccessToken(authCode.getClientID(), authCode.getUsername(), tokenRequest.getScope());
        RefreshToken refresh = util.getRefreshToken(authCode.getClientID(), authCode.getUsername(), tokenRequest.getScope());
        tokenStore.saveToken(access);

        logger.info("New Refresh Token:");
        logger.info(util.objectToString(refresh));
        logger.info("New Access Token:");
        logger.info(util.objectToString(access));

        return new TokenResponseDto(
                util.encodeObject(access),
                access.getType(),
                access.getExpiresIn(),
                util.encodeObject(refresh),
                access.getScope()
        );
    }

    public TokenResponseDto getRefreshedTokenAsJSON(TokenRequestDto tokenRequest) throws IOException, InvalidClientSecretException {
        validator.validate(tokenRequest);

        RefreshToken refresh = util.readRefreshTokenFromB64(tokenRequest.getRefreshToken());
        AccessToken access = util.getAccessToken(refresh.getClientID(), refresh.getUsername(), tokenRequest.getScope());
        tokenStore.saveToken(access);

        logger.info("Refresh Token [from request]:");
        logger.info(util.objectToString(refresh));
        logger.info("Refreshed Access Token:");
        logger.info(util.objectToString(access));

        return new TokenResponseDto(
                util.encodeObject(access),
                access.getType(),
                access.getExpiresIn(),
                access.getScope()
        );
    }
}