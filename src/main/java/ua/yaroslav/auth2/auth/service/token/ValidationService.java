package ua.yaroslav.auth2.auth.service.token;

import ua.yaroslav.auth2.auth.dto.AuthRequestDto;
import ua.yaroslav.auth2.auth.dto.LoginRequestDto;
import ua.yaroslav.auth2.auth.dto.TokenRequestDto;
import ua.yaroslav.auth2.auth.exception.LoginException;
import ua.yaroslav.auth2.auth.exception.Oauth2Exception;

import javax.servlet.http.HttpServletRequest;

public interface ValidationService {
    void validate(LoginRequestDto loginRequest) throws LoginException;
    void validate(AuthRequestDto authRequest) throws Oauth2Exception;
    void validate(TokenRequestDto tokenRequest) throws Oauth2Exception;
    void validate(HttpServletRequest request) throws Oauth2Exception;
}