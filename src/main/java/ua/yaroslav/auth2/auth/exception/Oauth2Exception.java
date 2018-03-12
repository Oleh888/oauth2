package ua.yaroslav.auth2.auth.exception;

public class Oauth2Exception extends RuntimeException {
    protected Exception cause;
    protected String type;


    public Oauth2Exception() {
    }

    public Oauth2Exception(String t) {
        this.type = t;
    }

    public Oauth2Exception(Exception e) {
        this.cause = e;
    }

    public Oauth2Exception(String type, Exception cause) {
        this.cause = cause;
        this.type = type;
    }

    public String toJSON() {
        if (cause == null) {
            return "{" + "\"error_type\":" + "\"" + type + "\"}";
        } else return "{" + "\"error_type\":" + "\"" + type + "\"," +
                    "\"error_description\":" + "\"" + cause.getMessage() + "\"" +
                "}";
    }

    public String toString() {
        return "Oauth2Exception: " + type.replace('_', ' ');
    }
}