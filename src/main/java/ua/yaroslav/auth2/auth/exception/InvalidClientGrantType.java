package ua.yaroslav.auth2.auth.exception;

public class InvalidClientGrantType extends AbstractException {
    private final String type = "invalid_client_grant_type";


    public InvalidClientGrantType() {
    }


    public String toString() {
        return "Invalid Client Grant Type";
    }

    public String toJSON() {
        return "{" + "\"error_type\":" + "\"" + type + "\"}";
    }
}