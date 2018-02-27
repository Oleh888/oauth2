package ua.yaroslav.auth2.authserver.jwt;
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
        return mapper.readValue(new String(Base64.getDecoder().decode(code.getBytes())), JWTAuthCode.class);
    }

    public JWTToken readTokenFromB64(String token) throws IOException {
        return mapper.readValue(new String(Base64.getDecoder().decode(token.getBytes())), JWTToken.class);
    }
}