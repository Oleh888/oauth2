package ua.yaroslav.auth2.authserver.json.entity;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class TokenAccess {
    private String clientID;
    private String username;
    private long expiresIn;
    private String scope;
    private String type;
    private int tokenID;


    public TokenAccess(){}
    public TokenAccess(@JsonProperty("client_id") String clientID,
                       @JsonProperty("username") String username,
                       @JsonProperty("expires_in") long expiresIn,
                       @JsonProperty("scope") String scope,
                       @JsonProperty("type") String type, int id) {
        this.clientID = clientID;
        this.username = username;
        this.expiresIn = expiresIn;
        this.scope = scope;
        this.type = type;
        this.tokenID = id;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getTokenID() {
        return tokenID;
    }

    public void setTokenID(int tokenID) {
        this.tokenID = tokenID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokenAccess tokenAccess = (TokenAccess) o;
        return expiresIn == tokenAccess.expiresIn &&
                Objects.equals(clientID, tokenAccess.clientID) &&
                Objects.equals(username, tokenAccess.username) &&
                Objects.equals(scope, tokenAccess.scope);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientID, username, expiresIn, scope);
    }
}