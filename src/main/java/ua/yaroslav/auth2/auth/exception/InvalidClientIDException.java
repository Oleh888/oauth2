package ua.yaroslav.auth2.auth.exception;

public class InvalidClientIDException extends AbstractException {
    public InvalidClientIDException() {
        type = "invalid_client_id";
    }

    public String toString(){
        return "Invalid Client ID!";
    }
}