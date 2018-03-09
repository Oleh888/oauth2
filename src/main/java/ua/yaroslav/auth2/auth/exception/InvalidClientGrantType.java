package ua.yaroslav.auth2.auth.exception;

public class InvalidClientGrantType extends AbstractException {
    public InvalidClientGrantType() {
        type = "invalid_client_grant_type";
    }

    public String toString() {
        return "Invalid Client Grant Type";
    }
}