package ua.yaroslav.auth2.authserver.jwt.entity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;

public abstract class JWTAbstract {
    public String toString() {
        try {
            return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getEncoded() {
        return Base64.getEncoder().encodeToString(this.toString().getBytes());
    }

    public String getDecoded() {
        return new String(Base64.getDecoder().decode(this.toString().getBytes()));
    }
}