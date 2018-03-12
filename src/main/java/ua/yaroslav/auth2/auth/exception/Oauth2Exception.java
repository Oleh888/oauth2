package ua.yaroslav.auth2.auth.exception;

import ua.yaroslav.auth2.auth.dto.ErrorResponseDto;

public class Oauth2Exception extends RuntimeException {
    private ErrorResponseDto err;


    public Oauth2Exception() {
    }

    public Oauth2Exception(ErrorType error, String error_description) {
        this.err = new ErrorResponseDto(error, error_description);
    }


    public String toString() {
        return "Oauth2Exception [" + err.getError() + "]: " + err.getErrorDescription();
    }

    public ErrorResponseDto getResponse() {
        return this.err;
    }
}