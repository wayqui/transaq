package com.wayqui.transaq.api.model;

import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
public class FindTransactionRequest {
    @NotNull(message = "account_iban cannot be null")
    @NotBlank(message = "account_iban cannot be empty")
    private String account_iban;

    private Boolean ascending;
}
