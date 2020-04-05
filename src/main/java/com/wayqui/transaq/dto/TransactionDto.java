package com.wayqui.transaq.dto;

import lombok.*;

import java.time.Instant;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    private String reference;
    private String account_iban;
    private Instant date;
    private Double amount;
    private Double fee;
    private String description;
}
