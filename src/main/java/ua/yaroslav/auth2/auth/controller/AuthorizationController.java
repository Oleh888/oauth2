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
import ua.yaroslav.auth2.auth.service.JSONUtil;
import ua.yaroslav.auth2.auth.service.iface.TokenService;
import ua.yaroslav.auth2.auth.service.iface.ValidationService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class AuthorizationController {
    private static final Logger logger = LoggerFactory.getLogger(AuthorizationController.class);
    private final TokenService tokenService;
    private final ValidationService validationService;
    private final JSONUtil util;


    public AuthorizationController(TokenService tokenService, ValidationService validationService, JSONUtil util) {
        this.tokenService = tokenService;
        this.validationService = validationService;
        this.util = util;
    }


    @PostMapping(value = {"/auth"}, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void getCode(AuthRequestDto authRequest, HttpServletResponse response) throws IOException {
        validationService.validate(authRequest);
        logger.info(authRequest.toString());
        response.sendRedirect(authRequest.getRedirectURI() +
                "?code=" + util.encodeObject(tokenService.getCode(authRequest)));
    }

    @GetMapping("/auth")
    public String getLogin(LoginRequestDto loginRequest, Model model) {
        validationService.validate(loginRequest);
        logger.info(loginRequest.toString());
        model.addAttribute("redirect_uri", loginRequest.getRedirectURI());
        model.addAttribute("client_id", loginRequest.getClientID());
        model.addAttribute("response_type", loginRequest.getResponseType());
        model.addAttribute("access_type", loginRequest.getAccessType());
        model.addAttribute("scope", loginRequest.getScope());
        return "login";
    }
}