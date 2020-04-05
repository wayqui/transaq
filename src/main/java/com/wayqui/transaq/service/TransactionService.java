package com.wayqui.transaq.service;

import com.wayqui.transaq.api.model.TransactionRequest;
import com.wayqui.transaq.api.model.TransactionResponse;
import com.wayqui.transaq.api.model.TransactionStatusRequest;
import com.wayqui.transaq.api.model.TransactionStatusResponse;

import java.util.List;

public interface TransactionService {

    <I> List<String> validate(I input);

    TransactionResponse createTransaction(TransactionRequest transaction);

    TransactionResponse findTransaction(String account_iban, Boolean ascending);

    TransactionStatusResponse obtainTransactionStatus(TransactionStatusRequest transactionStatus);
}
