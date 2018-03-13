package ua.yaroslav.auth2.auth.service.iface;

import ua.yaroslav.auth2.auth.dto.AuthRequestDto;
import ua.yaroslav.auth2.auth.dto.TokenRequestDto;
import ua.yaroslav.auth2.auth.dto.TokenResponseDto;
import ua.yaroslav.auth2.auth.entity.AuthCode;

import java.io.IOException;

public interface TokenService {
    AuthCode getCode(AuthRequestDto authRequest);
    TokenResponseDto getTokensAsJSON(TokenRequestDto tokenRequest) throws IOException;
    TokenResponseDto getRefreshedTokenAsJSON(TokenRequestDto tokenRequest) throws IOException;
}