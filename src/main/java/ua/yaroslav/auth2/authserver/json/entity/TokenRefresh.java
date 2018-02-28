package ua.yaroslav.auth2.authserver.json.entity;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenRefresh {
    private String clientID;
    private String username;
    private long expiresIn;
    private String type;


    public TokenRefresh(){}
    public TokenRefresh(@JsonProperty("client_id") String clientID,
                        @JsonProperty("username") String username,
                        @JsonProperty("expires_in") long expiresIn,
                        @JsonProperty("scope") String scope,
                        @JsonProperty("type") String type) {
        this.clientID = clientID;
        this.username = username;
        this.expiresIn = expiresIn;
        this.type = type;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}