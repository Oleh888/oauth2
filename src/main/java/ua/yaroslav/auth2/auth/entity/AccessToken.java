package ua.yaroslav.auth2.auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessToken {
    private String clientID;
    private String username;
    private long expiresIn;
    private String scope;
    private String type;
    private long time;


    public AccessToken() {
    }

    public AccessToken(@JsonProperty("client_id") String clientID,
                       @JsonProperty("username") String username,
                       @JsonProperty("expires_in") long expiresIn,
                       @JsonProperty("scope") String scope,
                       @JsonProperty("type") String type) {
        this.clientID = clientID;
        this.username = username;
        this.expiresIn = expiresIn;
        this.time = System.currentTimeMillis() + expiresIn;
        this.scope = scope;
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

    public long getTime() {
        return time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccessToken accessToken = (AccessToken) o;
        return expiresIn == accessToken.expiresIn &&
                Objects.equals(clientID, accessToken.clientID) &&
                Objects.equals(username, accessToken.username) &&
                Objects.equals(scope, accessToken.scope);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientID, username, expiresIn, scope);
    }
}