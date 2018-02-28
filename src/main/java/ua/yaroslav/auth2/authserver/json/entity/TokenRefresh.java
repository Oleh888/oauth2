package ua.yaroslav.auth2.authserver.json.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class TokenRefresh {
    private String clientID;
    private String username;
    private long expiresIn;
    private int accessTokenID;


    public TokenRefresh() {
    }

    public TokenRefresh(@JsonProperty("client_id") String clientID,
                        @JsonProperty("username") String username,
                        @JsonProperty("expires_in") long expiresIn) {
        this.clientID = clientID;
        this.username = username;
        this.expiresIn = expiresIn;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokenRefresh refresh = (TokenRefresh) o;
        return expiresIn == refresh.expiresIn &&
                Objects.equals(clientID, refresh.clientID) &&
                Objects.equals(username, refresh.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientID, username, expiresIn);
    }

    public void setAccessTokenID(int accessTokenID) {
        this.accessTokenID = accessTokenID;
    }

    public int getAccessTokenID() {
        return accessTokenID;
    }
}