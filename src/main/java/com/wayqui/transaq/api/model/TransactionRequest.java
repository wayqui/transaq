package com.wayqui.transaq.api.model;

import lombok.*;

import java.util.Date;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {

    private String reference;

    private String account_iban;

    private Date date;

    private Double amount;

    private Double fee;

    private String description;
}
