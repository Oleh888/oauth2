package ua.yaroslav.auth2.auth.exception;

public class LoginException extends RuntimeException {
    private String message;


    public LoginException(){

    }

    public LoginException(String message) {
        this.message = message;
    }


    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "LoginException: " + message;
    }
}