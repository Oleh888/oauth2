package ua.yaroslav.auth2.auth.exception;

public class InvalidClientAuthCodeException extends AbstractException {
    public InvalidClientAuthCodeException() {
        type = "invalid_client_authentication_code";
    }


    public String toString(){
        return "Invalid Client Authentication Code";
    }
}