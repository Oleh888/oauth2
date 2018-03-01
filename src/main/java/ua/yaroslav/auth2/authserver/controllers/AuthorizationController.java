package ua.yaroslav.auth2.authserver.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ua.yaroslav.auth2.authserver.dto.AuthRequestDto;
import ua.yaroslav.auth2.authserver.dto.LoginRequestDto;
import ua.yaroslav.auth2.authserver.json.JSONUtil;
import ua.yaroslav.auth2.authserver.json.entity.AuthCode;
import ua.yaroslav.auth2.store.InMemoryStore;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class AuthorizationController {
    @Value("${client.id}")
    private String CLIENT_ID;
    @Value("${client.secret}")
    private String CLIENT_SECRET;
    private final InMemoryStore store;
    private static final Logger logger = LoggerFactory.getLogger(AuthorizationController.class);

    public AuthorizationController(InMemoryStore store) {
        this.store = store;
    }


    @PostMapping(value = {"/auth"}, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void getCode(AuthRequestDto authRequest, HttpServletResponse response) throws IOException {
        logger.info("\n--------------------get-code-invocation---------------------\n");
        logger.info("client_id: \n\t" + authRequest.getClientID());
        logger.info("response_type: \n\t" + authRequest.getResponseType());
        logger.info("username: \n\t" + authRequest.getUsername());
        logger.info("password: \n\t" + authRequest.getPassword());
        logger.info("scope: \n\t" + "[" + authRequest.getScope() + "]\n");

        if (authRequest.getClientID().equals(CLIENT_ID)) {
            if (authRequest.getResponseType().equals("code")) {
                AuthCode code = JSONUtil.getCode(authRequest);
                store.addCode(code);
                String url = authRequest.getRedirectURI() + "?" +
                        "code=" + JSONUtil.encodeObject(code) + "&" +
                        "state=awesome";
                response.sendRedirect(url);
            }
        }
    }

    @GetMapping("/auth")
    public String getLogin(LoginRequestDto loginRequest, Model model, HttpServletResponse response) {
        model.addAttribute("redirect_uri", loginRequest.getRedirectURI());
        model.addAttribute("client_id", loginRequest.getClientID());
        model.addAttribute("response_type", loginRequest.getResponseType());
        model.addAttribute("access_type", loginRequest.getAccessType());
        model.addAttribute("scope", loginRequest.getScope());
        return "login";
    }
}