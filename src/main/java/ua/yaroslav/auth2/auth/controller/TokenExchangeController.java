package ua.yaroslav.auth2.auth.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.yaroslav.auth2.auth.dto.TokenRequestDto;
import ua.yaroslav.auth2.auth.dto.TokenResponseDto;
import ua.yaroslav.auth2.auth.token.Generator;

import java.io.IOException;

@RestController
public class TokenExchangeController {
    private final Generator generator;
    private static final Logger logger = LoggerFactory.getLogger(TokenExchangeController.class);


    public TokenExchangeController(Generator generator) {
        this.generator = generator;
    }


    @PostMapping(value = "/token", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenResponseDto> getToken(TokenRequestDto tokenRequest) throws IOException {
        if (tokenRequest.getGrantType().equals("authorization_code")) {
            logger.info(tokenRequest.toString());
            return new ResponseEntity<>(generator.createTokensAndGetJSON(tokenRequest), HttpStatus.OK);
        } else if (tokenRequest.getGrantType().equals("refresh_token")) {
            logger.info(tokenRequest.toString());
            return new ResponseEntity<>(generator.refreshTokenAndGetJSON(tokenRequest), HttpStatus.OK);
        } else {
            return ResponseEntity.badRequest().body(new TokenResponseDto());
        }
    }
}