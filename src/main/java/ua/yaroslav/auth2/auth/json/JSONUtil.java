package ua.yaroslav.auth2.auth.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;
import ua.yaroslav.auth2.auth.dto.AuthRequestDto;
import ua.yaroslav.auth2.entity.AccessToken;
import ua.yaroslav.auth2.entity.AuthCode;
import ua.yaroslav.auth2.entity.RefreshToken;

import java.io.IOException;
import java.util.Base64;
import java.util.Objects;

@Component
public class JSONUtil {
    private static ObjectMapper mapper;


    public JSONUtil() {
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }


    public ObjectMapper getMapper() {
        return mapper;
    }

    public AuthCode getCode(AuthRequestDto authRequest) {
        return new AuthCode(authRequest.getClientID(), authRequest.getUsername(), 15 * 1000 * 5);
    }

    public AccessToken getAccessToken(String clientID, String username, String scope) {
        if (StringUtils.isEmpty(scope)) scope = "grant_all";
        return new AccessToken(clientID, username, 60 * 1000 * 5, scope, "bearer");
    }

    public AuthCode readCodeFromB64(String code) throws IOException {
        String s = new String(Base64.getDecoder().decode(code.getBytes()));
        return mapper.readValue(s, AuthCode.class);
    }

    public AccessToken readTokenFromB64(String token) throws IOException {
        return mapper.readValue(new String(Base64.getDecoder().decode(token.getBytes())), AccessToken.class);
    }

    public String objectToString(Object code) {
        try {
            return "\n" + mapper.writeValueAsString(code) + "\n";
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String encodeObject(Object code) {
        return Base64.getEncoder().encodeToString(Objects.requireNonNull(objectToString(code)).getBytes());
    }

    public RefreshToken getRefreshToken(String clientID, String username, String scope) {
        return new RefreshToken(clientID, username, scope);
    }

    public RefreshToken readRefreshTokenFromB64(String token) throws IOException {
        return mapper.readValue(new String(Base64.getDecoder().decode(token.getBytes())), RefreshToken.class);
    }

    public static String responseToString(Object code){
        try {
            return "\n" + mapper.writeValueAsString(code) + "\n";
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}