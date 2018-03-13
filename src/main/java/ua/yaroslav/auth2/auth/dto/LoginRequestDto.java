package ua.yaroslav.auth2.auth.dto;

import ua.yaroslav.auth2.auth.service.JSONUtil;

public class LoginRequestDto {
    private String client_id;
    private String response_type;
    private String redirect_uri;
    private String access_type;
    private String scope;


    public LoginRequestDto() {

    }

    public LoginRequestDto(String client_id, String response_type, String redirect_uri, String access_type, String scope) {
        this.client_id = client_id;
        this.response_type = response_type;
        this.redirect_uri = redirect_uri;
        this.access_type = access_type;
        this.scope = scope;
    }


    public String getClientID() {
        return client_id;
    }

    public String getResponseType() {
        return response_type;
    }

    public String getRedirectURI() {
        return redirect_uri;
    }

    public String getAccessType() {
        return access_type;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public void setResponse_type(String response_type) {
        this.response_type = response_type;
    }

    public void setRedirect_uri(String redirect_uri) {
        this.redirect_uri = redirect_uri;
    }

    public void setAccess_type(String access_type) {
        this.access_type = access_type;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public String toString() {
        return JSONUtil.responseToString(this);
    }
}