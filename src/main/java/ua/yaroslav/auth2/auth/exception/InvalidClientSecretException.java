package ua.yaroslav.auth2.auth.exception;

public class InvalidClientSecretException extends AbstractException {
    private final String type = "invalid_client_secret";


    public InvalidClientSecretException() {
    }


    public String toString() {
        return "Invalid Client Secret";
    }

    public String toJSON() {
        return "{" + "\"error_type\":" + "\"" + type + "\"}";
    }
}