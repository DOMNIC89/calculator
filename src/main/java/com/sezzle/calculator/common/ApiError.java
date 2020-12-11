package com.sezzle.calculator.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sezzle.calculator.Severity;
import org.springframework.http.HttpStatus;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiError {

    @JsonProperty
    private String message;

    @JsonProperty
    private Severity severity;

    @JsonProperty
    private HttpStatus status;

    public ApiError(String message, Severity severity, HttpStatus status) {
        this.message = message;
        this.severity = severity;
        this.status = status;
    }
}
