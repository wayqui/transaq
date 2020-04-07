package com.wayqui.transaq.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ValidationDto {

    private String message;
    private List<String> errors;
}
