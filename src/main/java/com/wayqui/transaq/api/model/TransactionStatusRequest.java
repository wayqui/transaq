package com.wayqui.transaq.api.model;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Builder
@Getter
public class TransactionStatusRequest {

    @NotNull(message = "reference cannot be null")
    @NotBlank(message = "reference cannot be empty")
    private String reference;

    @Pattern(regexp = "CLIENT|ATM|INTERNAL", flags = Pattern.Flag.CASE_INSENSITIVE)
    private String channel;

}
