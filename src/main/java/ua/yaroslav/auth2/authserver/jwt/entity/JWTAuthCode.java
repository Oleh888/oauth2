package ua.yaroslav.auth2.authserver.jwt.entity;

public class JWTAuthCode extends JWTAbstract{
    private String clientID;
    private String username;
    private long expiresIn;

    public JWTAuthCode(){}
    public JWTAuthCode(String clientID, String username, long expiresIn) {
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
}