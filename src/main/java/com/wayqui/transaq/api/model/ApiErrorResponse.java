package com.wayqui.transaq.api.model;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class ApiErrorResponse {

    private String status;
    private Instant timestamp;
    private String message;
    private String debugMessage;
    private List<String> errors;

    private ApiErrorResponse() {
        timestamp = Instant.now();
    }

    public ApiErrorResponse(String status) {
        this();
        this.status = status;
    }

    public ApiErrorResponse(String status, Throwable ex) {
        this();
        this.status = status;
        this.message = "Unexpected error";
        this.debugMessage = ex.getLocalizedMessage();
    }

    public ApiErrorResponse(String status, String message) {
        this();
        this.status = status;
        this.message = message;
    }

    public ApiErrorResponse(String status, String message, List<String> errors) {
        this();
        this.status = status;
        this.message = message;
        this.errors = errors;
    }

    public ApiErrorResponse(String status, String message, Throwable ex, List<String> errors) {
        this();
        this.status = status;
        this.message = message;
        this.debugMessage = ex.getLocalizedMessage();
        this.errors = errors;
    }
}
