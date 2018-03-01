package ua.yaroslav.auth2.authserver.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ua.yaroslav.auth2.authserver.dto.TokenRequestDto;
import ua.yaroslav.auth2.authserver.json.JSONUtil;
import ua.yaroslav.auth2.authserver.json.entity.AuthCode;
import ua.yaroslav.auth2.authserver.json.entity.TokenAccess;
import ua.yaroslav.auth2.authserver.json.entity.TokenRefresh;
import ua.yaroslav.auth2.store.InMemoryStore;

import java.io.IOException;

@Controller
public class TokenExchangeController {
    @Value("${client.id}")
    private String CLIENT_ID;
    @Value("${client.secret}")
    private String CLIENT_SECRET;
    private final InMemoryStore store;
    private static final Logger logger = LoggerFactory.getLogger(TokenExchangeController.class);

    public TokenExchangeController(InMemoryStore store) {
        this.store = store;
    }

    @PostMapping(value = "/token", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getToken(TokenRequestDto tokenRequest) throws IOException {
        switch (tokenRequest.getGrantType()) {
            case "authorization_code": {
                logger.info("--------------------get-token-invocation[GT:" + tokenRequest.getGrantType() + "]--------------------\n");
                logger.info("client_id: \n\t" + tokenRequest.getClientID());
                logger.info("client_secret: \n\t" + tokenRequest.getClientSecret());
                logger.info("grant_type: \n\t" + tokenRequest.getGrantType());
                logger.info("code: \n\t" + tokenRequest.getCode());
                logger.info("scope: \n\t" + "[" + tokenRequest.getScope() + "]\n");

                AuthCode authCode = JSONUtil.readCodeFromB64(tokenRequest.getCode());
                TokenAccess access = JSONUtil.getAccessToken(authCode.getClientID(), authCode.getUsername(), tokenRequest.getScope());
                TokenRefresh refresh = JSONUtil.getRefreshToken(authCode.getClientID(), authCode.getUsername());
                store.addToken(access);

                logger.info("Refresh token as string after decode [AC] [" + refresh.getClass().getSimpleName() + "]:");
                logger.info(JSONUtil.objectToString(refresh));
                logger.info("Access token as string after decode [AC] [" + access.getClass().getSimpleName() + "]:");
                logger.info(JSONUtil.objectToString(access));

                return "{\n" +
                        "token_type: \"" + access.getType() + "\",\n" +
                        "access_token: \"" + JSONUtil.encodeObject(access) + "\",\n" +
                        "refresh_token: \"" + JSONUtil.encodeObject(refresh) + "\",\n" +
                        "expires_in: " + access.getExpiresIn() + "\n" +
                        "}";
            }
            case "refresh_token": {
                logger.info("--------------------get-token-invocation[GT:" + tokenRequest.getGrantType() + "]--------------------\n");

                TokenRefresh refresh = JSONUtil.readRefreshTokenFromB64(tokenRequest.getRefreshToken());

                logger.info("Refresh token as string after decode [RT] [" + refresh.getClass().getSimpleName() + "]:");
                logger.info("\t" + JSONUtil.objectToString(refresh));

                TokenAccess access = JSONUtil.getAccessToken(refresh.getClientID(), refresh.getUsername(), tokenRequest.getScope());
                store.addToken(access);

                return "{\n" +
                        "token_type: \"" + access.getType() + "\",\n" +
                        "access_token: \"" + JSONUtil.encodeObject(access) + "\",\n" +
                        "expires_in: " + access.getExpiresIn() + "\n" +
                        "}";
            }
            default: {
                return "{\"error\": \"invalid_grant_type\"}";
            }
        }
    }
}