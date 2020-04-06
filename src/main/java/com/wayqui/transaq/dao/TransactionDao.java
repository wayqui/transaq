package com.wayqui.transaq.dao;

import com.wayqui.transaq.dto.TransactionDto;

import java.util.List;

public interface TransactionDao {

    List<TransactionDto> findByReference(String reference);

    List<TransactionDto> findByIban(String account_iban);

    TransactionDto save(TransactionDto transactionDto);
}