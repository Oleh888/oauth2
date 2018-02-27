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
        return new JWTToken(clientID, username, new Date().getTime() + 3600, scope);
    }

    public JWTAuthCode readCodeFromB64(String code) throws IOException {
        System.out.println(code);
        String s = new String(Base64.getDecoder().decode(code.getBytes()));
        System.out.println(s);
        System.out.println();
        return mapper.readValue(s, JWTAuthCode.class);
    }

    public JWTToken readTokenFromB64(String token) throws IOException {
        return mapper.readValue(new String(Base64.getDecoder().decode(token.getBytes())), JWTToken.class);
    }

    public String objectToString(JWTAuthCode code){
        try {
            return new ObjectMapper().writeValueAsString(code);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String encodeObject(JWTAuthCode code){
        String s = objectToString(code);
        System.out.println("text before b64encode");
        System.out.println(s);
        return Base64.getEncoder().encodeToString(s.getBytes());
    }
}