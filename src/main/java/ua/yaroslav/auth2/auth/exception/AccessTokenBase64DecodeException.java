package ua.yaroslav.auth2.auth.exception;

public class AccessTokenBase64DecodeException extends AbstractException {
    private final String type = "access_token_base64_decode_exception";


    public AccessTokenBase64DecodeException(Exception e) {
        super(e);
    }


    public String toString() {
        return "Access Token Base64 Decode Exception";
    }

    public String toJSON() {
        return "{" + "\"error_type\":" + "\"" + type + "\"," +
                "\"error_cause\":" + "\"" + cause.getMessage() +
                "\"}";
    }
}