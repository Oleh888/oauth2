package ua.yaroslav.auth2.auth.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import ua.yaroslav.auth2.auth.service.implementation.JSONUtil;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenResponseDto {
    private String access_token;
    private String token_type;
    private long expires_in;
    private String refresh_token;
    private String scope;


    public TokenResponseDto() {
    }

    public TokenResponseDto(String access_token,
                            String token_type,
                            long expires_in,
                            String scope) {
        this.access_token = access_token;
        this.token_type = token_type;
        this.expires_in = expires_in;
        this.scope = scope;
    }

    @JsonCreator
    public TokenResponseDto(@JsonProperty("access_token") String access_token,
                            @JsonProperty("token_type") String token_type,
                            @JsonProperty("expires_in") long expires_in,
                            @JsonProperty("refresh_token") String refresh_token,
                            @JsonProperty("scope") String scope) {
        this.access_token = access_token;
        this.token_type = token_type;
        this.expires_in = expires_in;
        this.refresh_token = refresh_token;
        this.scope = scope;
    }


    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public void setExpires_in(long expires_in) {
        this.expires_in = expires_in;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @JsonProperty("access_token")
    public String getAccessToken() {
        return access_token;
    }

    public long getExpires_in() {
        return expires_in;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public String getScope() {
        return scope;
    }

    public String getToken_type() {
        return token_type;
    }

    @Override
    public String toString() {
        return JSONUtil.responseToString(this);
    }
}