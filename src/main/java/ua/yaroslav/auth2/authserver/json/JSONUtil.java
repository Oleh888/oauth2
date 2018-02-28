package ua.yaroslav.auth2.authserver.json;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import ua.yaroslav.auth2.authserver.FormData;
import ua.yaroslav.auth2.authserver.json.entity.AuthCode;
import ua.yaroslav.auth2.authserver.json.entity.TokenAccess;
import ua.yaroslav.auth2.authserver.json.entity.TokenRefresh;

import java.io.IOException;
import java.util.Base64;
import java.util.Date;

@Component
public class JSONUtil {
    private ObjectMapper mapper;

    public JSONUtil(){
        this.mapper = new ObjectMapper();
    }

    public AuthCode getCode(FormData formData){
        return new AuthCode(formData.getClientID(),formData.getUsername(),new Date().getTime() + 3600);
    }

    public TokenAccess getAccessToken(String clientID, String username , String scope){
        if (scope == "") scope = "grant_all";
        return new TokenAccess(clientID, username, new Date().getTime() + 60000, scope, "bearer");
    }

    public AuthCode readCodeFromB64(String code) throws IOException {
        String s = new String(Base64.getDecoder().decode(code.getBytes()));
        return mapper.readValue(s, AuthCode.class);
    }

    public TokenAccess readTokenFromB64(String token) throws IOException {
        return mapper.readValue(new String(Base64.getDecoder().decode(token.getBytes())), TokenAccess.class);
    }

    public String objectToString(Object code){
        try {
            return new ObjectMapper().writeValueAsString(code);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String encodeObject(Object code){
        String s = objectToString(code);
        System.out.println("JSON object as string after encoding [" + code.getClass().getSimpleName() + "]:");
        System.out.println("\t" + s);
        return Base64.getEncoder().encodeToString(s.getBytes());
    }

    public TokenRefresh getRefreshToken(String clientID, String username, int ath){
        TokenRefresh refresh = new TokenRefresh(clientID, username, new Date().getTime() + 60000 * 30);
        refresh.setAccessTokenHash(ath);
        return refresh;
    }

    public TokenRefresh readRefreshTokenFromB64(String token) throws IOException {
        return mapper.readValue(new String(Base64.getDecoder().decode(token.getBytes())), TokenRefresh.class);
    }
}