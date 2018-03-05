package ua.yaroslav.auth2.auth.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ua.yaroslav.auth2.auth.dto.AuthRequestDto;
import ua.yaroslav.auth2.auth.dto.LoginRequestDto;
import ua.yaroslav.auth2.auth.exception.InvalidClientAuthCodeException;
import ua.yaroslav.auth2.auth.exception.InvalidClientIDException;
import ua.yaroslav.auth2.auth.token.Generator;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class AuthorizationController {
    private final Generator generator;
    private static final Logger logger = LoggerFactory.getLogger(AuthorizationController.class);


    public AuthorizationController(Generator generator) {
        this.generator = generator;
    }


    @PostMapping(value = {"/auth"}, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void getCode(AuthRequestDto authRequest, Model model, HttpServletResponse response) throws IOException {
        logger.info(authRequest.toString());
        try {
            response.sendRedirect(generator.getURL(authRequest));
        } catch (InvalidClientAuthCodeException e) {
            logger.error(e.toString());
            model.addAttribute("exception", e.toString());
            response.sendRedirect("/error");
        }
    }

    @GetMapping("/auth")
    public String getLogin(LoginRequestDto loginRequest, Model model, HttpServletResponse response) {
        try {
            generator.getValidator().validate(loginRequest);
            model.addAttribute("redirect_uri", loginRequest.getRedirectURI());
            model.addAttribute("client_id", loginRequest.getClientID());
            model.addAttribute("response_type", loginRequest.getResponseType());
            model.addAttribute("access_type", loginRequest.getAccessType());
            model.addAttribute("scope", loginRequest.getScope());
            return "login";
        } catch (InvalidClientIDException e) {
            logger.error(e.toString());
            model.addAttribute("exception", e.toString());
            return "error";
        }
    }
}