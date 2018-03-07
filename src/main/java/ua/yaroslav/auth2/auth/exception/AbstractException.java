package ua.yaroslav.auth2.auth.exception;

public abstract class AbstractException extends Exception {
    protected Exception cause;


    public AbstractException() {
    }

    public AbstractException(Exception e) {
        this.cause = e;
    }


    public abstract String toJSON();
}