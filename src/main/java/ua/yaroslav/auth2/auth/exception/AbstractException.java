package ua.yaroslav.auth2.auth.exception;

public abstract class AbstractException extends Exception {
    protected Exception cause;
    protected String type;

    public AbstractException() {
    }

    public AbstractException(Exception e) {
        this.cause = e;
    }


    public String toJSON(){
        return "{" + "\"error_type\":" + "\"" + type + "\"}";
    }
}