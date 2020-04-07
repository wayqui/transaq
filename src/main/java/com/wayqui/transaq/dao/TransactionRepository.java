package com.wayqui.transaq.dao;

import com.wayqui.transaq.entity.Transaction;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {

    List<Transaction> findByReference(String reference);

    List<Transaction> findByIban(String account_iban);
}
