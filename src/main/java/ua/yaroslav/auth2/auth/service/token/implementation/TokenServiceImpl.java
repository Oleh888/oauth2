package ua.yaroslav.auth2.auth.service.token.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;
import ua.yaroslav.auth2.auth.dto.AuthRequestDto;
import ua.yaroslav.auth2.auth.dto.TokenRequestDto;
import ua.yaroslav.auth2.auth.dto.TokenResponseDto;
import ua.yaroslav.auth2.auth.entity.AccessToken;
import ua.yaroslav.auth2.auth.entity.AuthCode;
import ua.yaroslav.auth2.auth.entity.RefreshToken;
import ua.yaroslav.auth2.auth.exception.ErrorType;
import ua.yaroslav.auth2.auth.exception.Oauth2Exception;
import ua.yaroslav.auth2.auth.service.token.TokenService;

import java.io.IOException;
import java.util.Base64;
import java.util.Objects;

@Service
public class TokenServiceImpl implements TokenService {
    private final ObjectMapper mapper;
    private static final Logger logger = LoggerFactory.getLogger(TokenServiceImpl.class);


    public TokenServiceImpl(ObjectMapper mapper) {
        this.mapper = mapper;
    }


    @Override
    public String getCode(AuthRequestDto authRequest) throws Oauth2Exception {
        AuthCode code = new AuthCode(authRequest.getClientID(), authRequest.getUsername(), 15 * 1000 * 5);
        try {
            return encodeObject(code);
        } catch (JsonProcessingException e) {
            throw new Oauth2Exception(ErrorType.server_error, "Base64 Encode Exception");
        }
    }

    @Override
    public TokenResponseDto getTokensAsJSON(TokenRequestDto tokenRequest) throws IOException {
        AuthCode authCode = readCodeFromB64(tokenRequest.getCode());
        AccessToken access = createAccessToken(authCode.getClientID(), authCode.getUsername(), tokenRequest.getScope());
        RefreshToken refresh = createRefreshToken(authCode.getClientID(), authCode.getUsername(), tokenRequest.getScope());

        logger.info("New Refresh Token:");
        logger.info(objectAsJSON(refresh));
        logger.info("New Access Token:");
        logger.info(objectAsJSON(access));

        return new TokenResponseDto(
                encodeObject(access),
                access.getType(),
                access.getExpiresIn(),
                encodeObject(refresh),
                access.getScope()
        );
    }

    @Override
    public TokenResponseDto getRefreshedTokenAsJSON(TokenRequestDto tokenRequest) throws IOException {
        RefreshToken refresh = readRefreshTokenFromB64(tokenRequest.getRefreshToken());
        AccessToken access = createAccessToken(refresh.getClientID(), refresh.getUsername(), tokenRequest.getScope());

        logger.info("Refresh Token [from request]:");
        logger.info(objectAsJSON(refresh));
        logger.info("Refreshed Access Token:");
        logger.info(objectAsJSON(access));

        return new TokenResponseDto(
                encodeObject(access),
                access.getType(),
                access.getExpiresIn(),
                access.getScope()
        );
    }

    private AccessToken createAccessToken(String clientID, String username, String scope) {
        if (StringUtils.isEmpty(scope)) scope = "grant_all";
        return new AccessToken(clientID, username, 60 * 1000 * 5, scope, "bearer");
    }

    private RefreshToken createRefreshToken(String clientID, String username, String scope) {
        return new RefreshToken(clientID, username, scope);
    }

    private RefreshToken readRefreshTokenFromB64(String token) throws IOException {
        return mapper.readValue(new String(Base64.getDecoder().decode(token.getBytes())), RefreshToken.class);
    }

    private AccessToken readTokenFromB64(String token) throws IOException {
        return mapper.readValue(new String(Base64.getDecoder().decode(token.getBytes())), AccessToken.class);
    }

    private AuthCode readCodeFromB64(String code) throws IOException {
        return mapper.readValue(new String(Base64.getDecoder().decode(code.getBytes())), AuthCode.class);
    }

    private String objectAsJSON(Object code) throws JsonProcessingException {
        return mapper.writeValueAsString(code);
    }

    private String encodeObject(Object code) throws JsonProcessingException {
        return Base64.getEncoder().encodeToString(Objects.requireNonNull(objectAsJSON(code)).getBytes());
    }
}