package ua.yaroslav.auth2.auth.token;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;
import ua.yaroslav.auth2.auth.dto.AuthRequestDto;
import ua.yaroslav.auth2.auth.dto.LoginRequestDto;
import ua.yaroslav.auth2.auth.dto.TokenRequestDto;
import ua.yaroslav.auth2.store.InMemoryStore;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class Validator {
    private final InMemoryStore store;
    private final Logger logger = LoggerFactory.getLogger(Validator.class);

    public Validator(InMemoryStore store) {
        this.store = store;
    }

    public boolean validate(LoginRequestDto loginRequest) {
//        try {
//            System.out.println("Stored Client: " + store.getClients().size());
//            if (StringUtils.isEmpty(loginRequest.getRedirectURI())) {
//                logger.error("URL is not correct!");
//            } else {
//                HttpURLConnection connection = (HttpURLConnection) new URL(loginRequest.getRedirectURI()).openConnection();
//                connection.setRequestMethod("HEAD");
//                return store.checkClient(loginRequest.getClientID()) && connection.getResponseCode() == 200;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return false;
        System.out.println("Stored Client: " + store.getClients().size());
        return store.checkClient(loginRequest.getClientID());
    }

    public boolean validate(AuthRequestDto authRequest) {
        return store.checkClient(authRequest.getClientID()) && authRequest.getResponseType().equals("code");
    }

    public boolean validate(TokenRequestDto tokenRequest) {
        return store.checkClient(tokenRequest.getClientID(), tokenRequest.getClientSecret());
    }
}