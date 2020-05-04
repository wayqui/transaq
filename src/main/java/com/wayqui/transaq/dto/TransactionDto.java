package com.wayqui.transaq.dto;

import lombok.*;

import java.time.OffsetDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    private String reference;
    private String iban;
    private OffsetDateTime date;
    private Double amount;
    private Double fee;
    private String description;
}
