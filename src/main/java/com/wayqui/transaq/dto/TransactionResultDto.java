package com.wayqui.transaq.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
public class TransactionResultDto {
    private Optional<TransactionDto> transactionDto;
}
