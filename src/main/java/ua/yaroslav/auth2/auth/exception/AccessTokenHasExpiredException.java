package ua.yaroslav.auth2.auth.exception;

public class AccessTokenHasExpiredException extends AbstractException {
    public AccessTokenHasExpiredException() {
        type = "access_token_has_expired";
    }

    public String toString() {
        return "Access Token Has Expired";
    }
}