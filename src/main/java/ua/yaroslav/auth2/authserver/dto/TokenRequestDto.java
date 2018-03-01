package ua.yaroslav.auth2.authserver.dto;

public class TokenRequestDto {
    private String client_id;
    private String client_secret;
    private String grant_type;
    private String code;
    private String scope;
    private String refresh_token;


    public TokenRequestDto(){}
    public TokenRequestDto(String client_id, String client_secret, String grant_type, String code, String scope, String refresh_token) {
        this.client_id = client_id;
        this.client_secret = client_secret;
        this.grant_type = grant_type;
        this.code = code;
        this.scope = scope;
        this.refresh_token = refresh_token;
    }


    public String getClientID() {
        return client_id;
    }

    public String getClientSecret() {
        return client_secret;
    }

    public String getGrantType() {
        return grant_type;
    }

    public String getCode() {
        return code;
    }

    public String getScope() {
        return scope;
    }

    public String getRefreshToken() {
        return refresh_token;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public void setClient_secret(String client_secret) {
        this.client_secret = client_secret;
    }

    public void setGrant_type(String grant_type) {
        this.grant_type = grant_type;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String toString(){
        String rf = null;
        if(refresh_token != null) rf = refresh_token.substring(0,100);
        return  "\n" + this.getClass().getSimpleName() + ": " +
                "\n   client_id:     \n\t\t" + client_id +
                "\n   client_secret: \n\t\t" + client_secret +
                "\n   grant_type:    \n\t\t" + grant_type +
                "\n   code:          \n\t\t" + code +
                "\n   refresh_token: \n\t\t" + rf +
                "\n   scope:         \n\t\t" + "[" + scope + "]\n" ;
    }
}