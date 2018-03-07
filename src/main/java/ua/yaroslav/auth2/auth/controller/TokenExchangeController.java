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

import java.io.IOException;

@RestController
public class TokenExchangeController {
    private final Generator generator;
    private static final Logger logger = LoggerFactory.getLogger(TokenExchangeController.class);


    public TokenExchangeController(Generator generator) {
        this.generator = generator;
    }


    @PostMapping(value = "/token", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getToken(TokenRequestDto tokenRequest) throws IOException {
        logger.info(tokenRequest.toString());
        if (tokenRequest.getGrantType().equals("authorization_code")) {

            try {
                return ResponseEntity.ok().body(generator.getTokensAsJSON(tokenRequest));
            } catch (InvalidClientSecretException e) {
                logger.error(e.toString());
                return ResponseEntity.badRequest().body(e.toJSON());
            } catch (InvalidClientIDException e) {
                logger.error(e.toString());
                return ResponseEntity.badRequest().body(e.toJSON());
            }

        } else if (tokenRequest.getGrantType().equals("refresh_token")) {

            try {
                return ResponseEntity.ok().body(generator.getRefreshedTokenAsJSON(tokenRequest));
            } catch (InvalidClientSecretException e) {
                logger.error(e.toString());
                return ResponseEntity.badRequest().body(e.toJSON());
            } catch (InvalidClientIDException e) {
                logger.error(e.toString());
                return ResponseEntity.badRequest().body(e.toJSON());
            }

        } else {
            return ResponseEntity.badRequest().body(new InvalidClientGrantType().toJSON());
        }
    }
}