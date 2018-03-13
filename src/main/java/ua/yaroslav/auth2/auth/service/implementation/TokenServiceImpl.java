package ua.yaroslav.auth2.auth.service.implementation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ua.yaroslav.auth2.auth.dto.AuthRequestDto;
import ua.yaroslav.auth2.auth.dto.TokenRequestDto;
import ua.yaroslav.auth2.auth.dto.TokenResponseDto;
import ua.yaroslav.auth2.auth.entity.AccessToken;
import ua.yaroslav.auth2.auth.entity.AuthCode;
import ua.yaroslav.auth2.auth.entity.RefreshToken;
import ua.yaroslav.auth2.auth.exception.Oauth2Exception;
import ua.yaroslav.auth2.auth.service.TokenService;
import ua.yaroslav.auth2.auth.store.CodeRepository;
import ua.yaroslav.auth2.auth.store.TokenRepository;

import java.io.IOException;

@Service
public class TokenServiceImpl implements TokenService {
    private final TokenRepository tokenRepository;
    private final CodeRepository codeRepository;
    private final JSONUtil util;
    private static final Logger logger = LoggerFactory.getLogger(TokenServiceImpl.class);


    public TokenServiceImpl(JSONUtil util, TokenRepository tokenRepository, CodeRepository codeRepository) {
        this.tokenRepository = tokenRepository;
        this.codeRepository = codeRepository;
        this.util = util;
    }


    @Override
    public AuthCode getCode(AuthRequestDto authRequest) throws Oauth2Exception {
        AuthCode code = util.getCode(authRequest);
        codeRepository.save(code);
        return code;
    }

    @Override
    public TokenResponseDto getTokensAsJSON(TokenRequestDto tokenRequest) throws IOException {
        AuthCode authCode = util.readCodeFromB64(tokenRequest.getCode());
        AccessToken access = util.getAccessToken(authCode.getClientID(), authCode.getUsername(), tokenRequest.getScope());
        RefreshToken refresh = util.getRefreshToken(authCode.getClientID(), authCode.getUsername(), tokenRequest.getScope());
        tokenRepository.save(access);

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

    @Override
    public TokenResponseDto getRefreshedTokenAsJSON(TokenRequestDto tokenRequest) throws IOException {
        RefreshToken refresh = util.readRefreshTokenFromB64(tokenRequest.getRefreshToken());
        AccessToken access = util.getAccessToken(refresh.getClientID(), refresh.getUsername(), tokenRequest.getScope());
        tokenRepository.save(access);

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