package ua.yaroslav.auth2.auth.entity;

public class Client {
    private String name;
    private String secret;


    public Client() {

    }

    public Client(String name, String secret) {
        this.name = name;
        this.secret = secret;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}