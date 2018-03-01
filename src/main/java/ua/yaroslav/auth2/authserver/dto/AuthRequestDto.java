package ua.yaroslav.auth2.authserver.dto;

public class AuthRequestDto {
    private String username;
    private String password;
    private String client_id;
    private String response_type;
    private String redirect_uri;
    private String scope;


    public AuthRequestDto() {
    }

    public AuthRequestDto(String username, String password, String client_id, String response_type, String redirect_uri, String scope) {
        this.username = username;
        this.password = password;
        this.client_id = client_id;
        this.response_type = response_type;
        this.redirect_uri = redirect_uri;
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

    public String getRedirectURI() {
        return redirect_uri;
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

    public void setRedirect_uri(String redirect_uri) {
        this.redirect_uri = redirect_uri;
    }

    public String toString(){
        return  "client_id: \n\t" + client_id +
                "\nresponse_type: \n\t" + response_type +
                "\nusername: \n\t" + username +
                "\npassword: \n\t" + password +
                "\nscope: \n\t" + "[" + scope + "]\n" ;
    }
}