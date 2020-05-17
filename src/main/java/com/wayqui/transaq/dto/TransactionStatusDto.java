package com.wayqui.transaq.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
public class TransactionStatusDto {
    private String reference;
    private TransactionStatus status;
    private BigDecimal amount;
    private BigDecimal fee;
}
