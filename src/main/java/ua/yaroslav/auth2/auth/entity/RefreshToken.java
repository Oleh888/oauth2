package ua.yaroslav.auth2.auth.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class RefreshToken {
    private String clientID;
    private String username;
    private int accessTokenID;
    private String scope;


    public RefreshToken() {
    }

    public RefreshToken(@JsonProperty("client_id") String clientID,
                        @JsonProperty("username") String username,
                        @JsonProperty("scope") String scope) {
        this.clientID = clientID;
        this.username = username;
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

    public void setAccessTokenID(int accessTokenID) {
        this.accessTokenID = accessTokenID;
    }

    public int getAccessTokenID() {
        return accessTokenID;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RefreshToken that = (RefreshToken) o;
        return accessTokenID == that.accessTokenID &&
                Objects.equals(clientID, that.clientID) &&
                Objects.equals(username, that.username) &&
                Objects.equals(scope, that.scope);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientID, username, accessTokenID, scope);
    }
}