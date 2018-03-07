package ua.yaroslav.auth2.auth.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.yaroslav.auth2.auth.dto.TokenRequestDto;
import ua.yaroslav.auth2.auth.exception.InvalidClientGrantType;
import ua.yaroslav.auth2.auth.exception.InvalidClientIDException;
import ua.yaroslav.auth2.auth.exception.InvalidClientSecretException;
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

        try {
            validator.validate(tokenRequest);
        } catch (InvalidClientSecretException e) {
            logger.error(e.toString());
            return ResponseEntity.badRequest().body(e.toJSON());
        } catch (InvalidClientIDException e) {
            logger.error(e.toString());
            return ResponseEntity.badRequest().body(e.toJSON());
        } catch (InvalidClientGrantType e) {
            logger.error(e.toString());
            return ResponseEntity.badRequest().body(e.toJSON());
        }

        if (tokenRequest.getGrantType().equals("authorization_code")) {
            return ResponseEntity.ok().body(generator.getTokensAsJSON(tokenRequest));
        } else if (tokenRequest.getGrantType().equals("refresh_token")) {
            return ResponseEntity.ok().body(generator.getRefreshedTokenAsJSON(tokenRequest));
        } else {
            return ResponseEntity.badRequest().body(new InvalidClientGrantType().toJSON());
        }
    }
}