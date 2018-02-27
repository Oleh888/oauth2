package ua.yaroslav.auth2.authserver;

public class FormData {
    private String username;
    private String password;
    private String client_id;
    private String response_type;
    private String scope;

    public FormData(){}
    public FormData(String username, String password, String client_id, String redirect_uri, String response_type, String scope) {
        this.username = username;
        this.password = password;
        this.client_id = client_id;
        this.response_type = response_type;
        this.scope = scope;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getClientID() {
        return client_id;
    }

    public String getResponseType() {
        return response_type;
    }

    public String getScope() {
        return scope;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public void setResponse_type(String response_type) {
        this.response_type = response_type;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}