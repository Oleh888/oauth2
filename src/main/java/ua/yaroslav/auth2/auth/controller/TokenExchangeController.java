package ua.yaroslav.auth2.auth.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.yaroslav.auth2.auth.dto.TokenRequestDto;
import ua.yaroslav.auth2.auth.exception.ErrorType;
import ua.yaroslav.auth2.auth.exception.Oauth2Exception;
import ua.yaroslav.auth2.auth.service.token.TokenService;
import ua.yaroslav.auth2.auth.service.token.ValidationService;

import java.io.IOException;

@RestController
public class TokenExchangeController {
    private static final Logger logger = LoggerFactory.getLogger(TokenExchangeController.class);
    private final TokenService tokenService;
    private final ValidationService validationService;


    public TokenExchangeController(TokenService tokenService, ValidationService validationService) {
        this.tokenService = tokenService;
        this.validationService = validationService;
    }


    @PostMapping(value = "/token", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getToken(TokenRequestDto tokenRequest) throws IOException {
        logger.info(tokenRequest.toString());
        validationService.validate(tokenRequest);

        if (tokenRequest.getGrantType().equals("authorization_code")) {
            return ResponseEntity.ok().body(tokenService.getTokensAsJSON(tokenRequest));
        } else if (tokenRequest.getGrantType().equals("refresh_token")) {
            return ResponseEntity.ok().body(tokenService.getRefreshedTokenAsJSON(tokenRequest));
        } else {
            throw new Oauth2Exception(ErrorType.invalid_request, "Invalid Grant Type");
        }
    }
}