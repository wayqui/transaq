package com.wayqui.transaq.api.model;

import lombok.*;

import java.io.Serializable;
import java.time.Instant;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TransactionResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private String reference;
    private String account_iban;
    private Instant date;
    private Double amount;
    private Double fee;
    private String description;
}
