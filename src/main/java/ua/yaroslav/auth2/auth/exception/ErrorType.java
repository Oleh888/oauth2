package ua.yaroslav.auth2.auth.exception;

public enum ErrorType {
    invalid_request,
    unauthorized_client,
    access_denied,
    unsupported_response_type,
    invalid_scope,
    server_error,
    temporarily_unavailable
}