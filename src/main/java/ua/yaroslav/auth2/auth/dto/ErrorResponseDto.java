package ua.yaroslav.auth2.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import ua.yaroslav.auth2.auth.exception.ErrorType;

public class ErrorResponseDto {
    private ErrorType error;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String error_description;

    public ErrorResponseDto() {

    }

    public ErrorResponseDto(@JsonProperty("error") ErrorType error,
                            @JsonProperty("error_description") String error_description) {
        this.error = error;
        this.error_description = error_description;
    }

    public ErrorType getError() {
        return error;
    }

    public void setError(ErrorType error) {
        this.error = error;
    }

    @JsonProperty("error_description")
    public String getErrorDescription() {
        return error_description;
    }

    public void setError_description(String error_description) {
        this.error_description = error_description;
    }
}