package ua.yaroslav.auth2.authserver.jwt.entity;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Base64;

public class JWTToken {
    private String clientID;
    private String username;
    private long expiresIn;
    private String scope;

    public JWTToken(){}
    public JWTToken(@JsonProperty("client_id") String clientID,
                    @JsonProperty("username") String username,
                    @JsonProperty("expires_in") long expiresIn,
                    @JsonProperty("scope") String scope) {
        this.clientID = clientID;
        this.username = username;
        this.expiresIn = expiresIn;
        this.scope = scope;
    }


    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getEncoded() {
        return Base64.getEncoder().encodeToString(this.toString().getBytes());
    }

    public String getDecoded() {
        return new String(Base64.getDecoder().decode(this.toString().getBytes()));
    }
}