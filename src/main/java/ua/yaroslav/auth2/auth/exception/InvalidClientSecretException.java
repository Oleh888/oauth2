package ua.yaroslav.auth2.auth.exception;

public class InvalidClientSecretException extends AbstractException {
    public InvalidClientSecretException() {
        type = "invalid_client_secret";
    }

    public String toString() {
        return "Invalid Client Secret";
    }
}