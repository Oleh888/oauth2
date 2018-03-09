package ua.yaroslav.auth2.auth.exception;

public class AccessTokenInvalidException extends AbstractException {
    public AccessTokenInvalidException() {
        type = "invalid_access_token";
    }

    public String toString() {
        return "Invalid Access Token";
    }
}