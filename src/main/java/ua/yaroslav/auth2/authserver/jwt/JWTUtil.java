package ua.yaroslav.auth2.authserver.jwt;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import ua.yaroslav.auth2.authserver.FormData;
import ua.yaroslav.auth2.authserver.jwt.entity.JWTAuthCode;
import ua.yaroslav.auth2.authserver.jwt.entity.JWTToken;

import java.io.IOException;
import java.util.Base64;
import java.util.Date;

@Component
public class JWTUtil {
    private ObjectMapper mapper;

    public JWTUtil(){
        this.mapper = new ObjectMapper();
    }

    public JWTAuthCode getCode(FormData formData){
        return new JWTAuthCode(formData.getClientID(),formData.getUsername(),new Date().getTime() + 3600);
    }

    public JWTToken getToken(String clientID, String username , String scope){
        if (scope == "") scope = "grant_all";
        return new JWTToken(clientID, username, new Date().getTime() + 3600, scope, "bearer");
    }

    public JWTAuthCode readCodeFromB64(String code) throws IOException {
        String s = new String(Base64.getDecoder().decode(code.getBytes()));
        return mapper.readValue(s, JWTAuthCode.class);
    }

    public JWTToken readTokenFromB64(String token) throws IOException {
        return mapper.readValue(new String(Base64.getDecoder().decode(token.getBytes())), JWTToken.class);
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
        System.out.println("JSON object as string after encoding:");
        System.out.println("\t" + s);
        return Base64.getEncoder().encodeToString(s.getBytes());
    }
}