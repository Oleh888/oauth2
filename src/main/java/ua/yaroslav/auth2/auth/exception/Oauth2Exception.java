package ua.yaroslav.auth2.auth.exception;

import ua.yaroslav.auth2.auth.dto.ErrorResponseDto;

public class Oauth2Exception extends RuntimeException {
    private ErrorType error;
    private String error_description;


    public Oauth2Exception() {
    }

    public Oauth2Exception(ErrorType error, String error_description) {
        this.error = error;
        this.error_description = error_description;
    }


    @Override
    public String toString() {
        return "Oauth2Exception [" + error + "]: " + error_description;
    }

    public ErrorResponseDto getResponse() {
        return new ErrorResponseDto(error, error_description);
    }
}