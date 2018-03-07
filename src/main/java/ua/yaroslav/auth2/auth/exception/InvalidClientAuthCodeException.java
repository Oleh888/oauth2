package ua.yaroslav.auth2.auth.exception;

public class InvalidClientAuthCodeException extends AbstractException {
    private final String type = "invalid_client_authentication_code";


    public InvalidClientAuthCodeException() {
    }


    public String toString(){
        return "Invalid Client Authentication Code";
    }

    public String toJSON(){
        return "{" + "\"error_type\":" + "\"" + type + "\"}";
    }
}