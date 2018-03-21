package ua.yaroslav.auth2.auth.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ua.yaroslav.auth2.auth.dto.ErrorResponseDto;
import ua.yaroslav.auth2.auth.exception.LoginException;
import ua.yaroslav.auth2.auth.exception.Oauth2Exception;

@ControllerAdvice
public class AuthorizationExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(AuthorizationExceptionHandler.class);

    @ExceptionHandler(Oauth2Exception.class)
    public ResponseEntity<?> handleOauth2Exception(Oauth2Exception oe) {
        logger.error(oe.toString());
        return ResponseEntity.badRequest().body(new ErrorResponseDto(oe.getError(), oe.getErrorDescription()));
    }

    @ExceptionHandler(LoginException.class)
    public String handleLoginException(LoginException le, Model model) {
        logger.error(le.toString());
        model.addAttribute("exception", le.toString());
        return "error";
    }
}