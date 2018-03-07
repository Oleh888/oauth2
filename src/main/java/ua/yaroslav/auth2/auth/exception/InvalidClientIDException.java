package ua.yaroslav.auth2.auth.exception;

public class InvalidClientIDException extends AbstractException {
    private final String type = "invalid_client_id";


    public InvalidClientIDException() {
    }


    public String toString(){
        return "Invalid Client ID!";
    }

    public String toJSON(){
        return "{" + "\"error_type\":" + "\"" + type + "\"}";
    }
}