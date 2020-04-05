package com.wayqui.transaq.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TransactionStatusDto {
    private String reference;
    private TransactionStatus status;
}
