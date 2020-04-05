package com.wayqui.transaq.service;

import com.wayqui.transaq.api.model.TransactionResponse;
import com.wayqui.transaq.dto.TransactionChannel;
import com.wayqui.transaq.dto.TransactionDto;
import com.wayqui.transaq.dto.TransactionResultDto;
import com.wayqui.transaq.dto.TransactionStatusDto;

import java.util.List;
import java.util.Optional;

public interface TransactionService {

    <I> List<String> validate(I input);

    TransactionResultDto createTransaction(TransactionDto transaction);

    TransactionResponse findTransaction(String account_iban, Boolean ascending);

    Optional<TransactionStatusDto> obtainTransactionStatus(String reference, TransactionChannel channel);
}
