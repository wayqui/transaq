package com.wayqui.transaq.api.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FindTransactionRequest {

    private String account_iban;
    private Boolean ascending;
}
