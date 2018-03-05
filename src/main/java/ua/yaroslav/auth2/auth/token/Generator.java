package ua.yaroslav.auth2.auth.token;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ua.yaroslav.auth2.auth.dto.AuthRequestDto;
import ua.yaroslav.auth2.auth.dto.TokenRequestDto;
import ua.yaroslav.auth2.auth.entity.AccessToken;
import ua.yaroslav.auth2.auth.entity.AuthCode;
import ua.yaroslav.auth2.auth.entity.RefreshToken;
import ua.yaroslav.auth2.auth.json.JSONUtil;
import ua.yaroslav.auth2.store.InMemoryStore;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class Generator {
    private final Validator validator;
    private final InMemoryStore store;
    private final JSONUtil util;
    private static final Logger logger = LoggerFactory.getLogger(Generator.class);


    public Generator(Validator validator, InMemoryStore store, JSONUtil util) {
        this.validator = validator;
        this.store = store;
        this.util = util;
    }


    public Validator getValidator() {
        return validator;
    }

    public String createCodeAndGetURL(AuthRequestDto authRequest) {
        if (validator.validate(authRequest)) {
            AuthCode code = util.getCode(authRequest);
            store.addCode(code);

            URLBuilder builder = new URLBuilder();
            return builder
                    .setRedirect(authRequest.getRedirectURI())
                    .setCode(util.encodeObject(code))
                    .setState("awesome")
                    .build();
        }
        return null;
    }

    public Map<String, String> createTokensAndGetJSON(TokenRequestDto tokenRequest) throws IOException {
        Map<String, String> json = new HashMap<>();
        if (validator.validate(tokenRequest)) {
            AuthCode authCode = util.readCodeFromB64(tokenRequest.getCode());
            AccessToken access = util.getAccessToken(authCode.getClientID(), authCode.getUsername(), tokenRequest.getScope());
            RefreshToken refresh = util.getRefreshToken(authCode.getClientID(), authCode.getUsername(), tokenRequest.getScope());
            store.addToken(access);

            logger.info("New Refresh Token:");
            logger.info(util.objectToString(refresh));
            logger.info("New Access Token:");
            logger.info(util.objectToString(access));

            json.put("access_token", util.encodeObject(access));
            json.put("token_type", access.getType());
            json.put("expires_in", String.valueOf(access.getExpiresIn()));
            json.put("refresh_token", util.encodeObject(refresh));
            json.put("scope", access.getScope());
        } else {
            json.put("error", "bad_request");
        }

        System.out.println("BEST JSON:");
        System.out.println(util.getMapper().writeValueAsString(json));

        return json;
    }

    public String createTokensAndGetText(TokenRequestDto tokenRequest) throws IOException {
        StringBuilder builder = new StringBuilder();
        if (validator.validate(tokenRequest)) {
            AuthCode authCode = util.readCodeFromB64(tokenRequest.getCode());
            AccessToken access = util.getAccessToken(authCode.getClientID(), authCode.getUsername(), tokenRequest.getScope());
            RefreshToken refresh = util.getRefreshToken(authCode.getClientID(), authCode.getUsername(), tokenRequest.getScope());
            store.addToken(access);

            logger.info("New Refresh Token:");
            logger.info(util.objectToString(refresh));
            logger.info("New Access Token:");
            logger.info(util.objectToString(access));

            builder.append("{\n");
            builder.append("\"access_token\":\"").append(util.encodeObject(access)).append("\",\n");
            builder.append("\"token_type\":\"").append(access.getType()).append("\",\n");
            builder.append("\"expires_in\":").append(String.valueOf(access.getExpiresIn())).append(",\n");
            builder.append("\"refresh_token\":\"").append(util.encodeObject(refresh)).append("\",\n");
            builder.append("\"scope\":\"").append(access.getScope()).append("\",\n");
            builder.append("\"\n}");

        } else {
            builder.append("{\"error:\"").append("\"bad_request\"").append("}");
        }
        return builder.toString();
    }

    public Map<String, String> refreshTokenAndGetJSON(TokenRequestDto tokenRequest) throws IOException {
        Map<String, String> json = new HashMap<>();
        if (validator.validate(tokenRequest)) {
            RefreshToken refresh = util.readRefreshTokenFromB64(tokenRequest.getRefreshToken());
            logger.info("Refresh Token [from request]:");
            logger.info(util.objectToString(refresh));

            AccessToken access = util.getAccessToken(refresh.getClientID(), refresh.getUsername(), tokenRequest.getScope());
            store.addToken(access);
            logger.info("Refreshed Access Token:");
            logger.info(util.objectToString(access));

            json.put("token_type", access.getType());
            json.put("access_token", util.encodeObject(access));
            json.put("expires_in", String.valueOf(access.getExpiresIn()));
        } else {
            json.put("error", "bad_request");
        }
        return json;
    }

    class URLBuilder {
        private String url;


        public URLBuilder() {

        }

        public URLBuilder setRedirect(String redirect) {
            url = redirect;
            return this;
        }


        public URLBuilder setCode(String code) {
            url = url + "?code=" + code;
            return this;
        }

        public URLBuilder setState(String state) {
            url = url + "&state=" + state;
            return this;
        }

        public String build() {
            return url;
        }
    }
}