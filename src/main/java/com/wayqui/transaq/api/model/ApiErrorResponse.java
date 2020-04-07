package com.wayqui.transaq.api.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
public class ApiErrorResponse {

    private String status;
    private Instant timestamp;
    private String message;
    private String debugMessage;
    private List<String> errors;
}
