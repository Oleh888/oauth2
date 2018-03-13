package ua.yaroslav.auth2.auth.exception;

public class Oauth2Exception extends RuntimeException {
    private ErrorType error;
    private String error_description;


    public Oauth2Exception() {
    }

    public Oauth2Exception(ErrorType error, String error_description) {
        this.error = error;
        this.error_description = error_description;
    }


    public ErrorType getError() {
        return error;
    }

    public String getErrorDescription() {
        return error_description;
    }

    @Override
    public String toString() {
        return "Oauth2Exception [" + error + "]: " + error_description;
    }
}