package com.wayqui.transaq.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class TransactionStatusDto {
    private String reference;
    private TransactionStatus status;
    private Double amount;
    private Double fee;
}
