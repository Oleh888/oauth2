package ua.yaroslav.auth2.auth.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ua.yaroslav.auth2.auth.dto.TokenRequestDto;
import ua.yaroslav.auth2.auth.token.Generator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class TokenExchangeController {
    private final Generator generator;
    private static final Logger logger = LoggerFactory.getLogger(TokenExchangeController.class);


    public TokenExchangeController(Generator generator) {
        this.generator = generator;
    }


    @PostMapping(value = "/token", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, String>> getToken(TokenRequestDto tokenRequest) throws IOException {
        if (tokenRequest.getGrantType().equals("authorization_code")) {
            logger.info(tokenRequest.toString());
            return new ResponseEntity<>(generator.createTokensAndGetJSON(tokenRequest), HttpStatus.OK);
        } else if (tokenRequest.getGrantType().equals("refresh_token")) {
            logger.info(tokenRequest.toString());
            return new ResponseEntity<>(generator.refreshTokenAndGetJSON(tokenRequest), HttpStatus.OK);
        } else {
            Map<String, String> json = new HashMap<>();
            json.put("error", "invalid_grant_type");
            return new ResponseEntity<>(json, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/token2", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, String>> getJSON(){
        Map<String, String> json = new HashMap<>();
        json.put("token_type", "token_maafaka");
        json.put("access_token", "access_maafaka");
        return new ResponseEntity<>(json, HttpStatus.OK);
    }
}