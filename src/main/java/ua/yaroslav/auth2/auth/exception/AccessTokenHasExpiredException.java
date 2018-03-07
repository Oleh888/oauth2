package ua.yaroslav.auth2.auth.exception;

public class AccessTokenHasExpiredException extends Throwable {
    private final String type = "access_token_has_expired";

    public AccessTokenHasExpiredException() {
    }

    public String toString() {
        return "Access Token Has Expired";
    }

    public String toJSON() {
        return "{" + "\"error_type\":" + "\"" + type + "\"}";
    }
}