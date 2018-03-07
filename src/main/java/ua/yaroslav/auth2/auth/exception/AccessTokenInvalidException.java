package ua.yaroslav.auth2.auth.exception;

public class AccessTokenInvalidException extends AbstractException {
    private final String type = "invalid_access_token";


    public AccessTokenInvalidException() {
    }


    public String toString() {
        return "Invalid Access Token";
    }

    public String toJSON() {
        return "{" + "\"error_type\":" + "\"" + type + "\"}";
    }
}