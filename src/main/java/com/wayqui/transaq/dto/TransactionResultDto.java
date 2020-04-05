package com.wayqui.transaq.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
public class TransactionResultDto {
    private Boolean created;
    private Optional<TransactionDto> transactionDto;
}
