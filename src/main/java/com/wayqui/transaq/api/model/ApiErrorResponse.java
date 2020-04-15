package com.wayqui.transaq.api.model;

import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ApiErrorResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private String status;
    private Instant timestamp;
    private String message;
    private String debugMessage;
    private List<String> errors;
}
