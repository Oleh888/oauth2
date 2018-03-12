package ua.yaroslav.auth2.auth.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.yaroslav.auth2.auth.dto.TokenRequestDto;
import ua.yaroslav.auth2.auth.exception.Oauth2Exception;
import ua.yaroslav.auth2.auth.token.Generator;
import ua.yaroslav.auth2.auth.token.Validator;

import java.io.IOException;

@RestController
public class TokenExchangeController {
    private static final Logger logger = LoggerFactory.getLogger(TokenExchangeController.class);
    private final Generator generator;
    private final Validator validator;


    public TokenExchangeController(Generator generator, Validator validator) {
        this.generator = generator;
        this.validator = validator;
    }


    @PostMapping(value = "/token", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getToken(TokenRequestDto tokenRequest) throws IOException {
        logger.info(tokenRequest.toString());
        validator.validate(tokenRequest);

        if (tokenRequest.getGrantType().equals("authorization_code")) {
            return ResponseEntity.ok().body(generator.getTokensAsJSON(tokenRequest));
        } else if (tokenRequest.getGrantType().equals("refresh_token")) {
            return ResponseEntity.ok().body(generator.getRefreshedTokenAsJSON(tokenRequest));
        } else {
            return ResponseEntity.badRequest().body("invalid_client_grant_type");
        }
    }

    @ExceptionHandler(Oauth2Exception.class)
    public ResponseEntity<?> handleOauth2Exception(Oauth2Exception oe){
        logger.error(oe.toString());
        return ResponseEntity.badRequest().body(oe.toJSON());
    }
}