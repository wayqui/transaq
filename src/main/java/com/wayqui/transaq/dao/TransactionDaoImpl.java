package com.wayqui.transaq.dao;

import com.wayqui.transaq.conf.mapper.TransactionMapper;
import com.wayqui.transaq.dto.TransactionDto;
import com.wayqui.transaq.entity.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransactionDaoImpl implements TransactionDao {

    @Autowired
    TransactionRepository repository;

    @Override
    public List<TransactionDto> findByReference(String reference) {
        List<Transaction> transactionsByReference = repository.findByReference(reference);
        return TransactionMapper.INSTANCE.entitiesToDtos(transactionsByReference);
    }

    @Override
    public List<TransactionDto> findByIban(String account_iban) {
        List<Transaction> transactionsByReference = repository.findByIban(account_iban);

        return TransactionMapper.INSTANCE.entitiesToDtos(transactionsByReference);
    }

    @Override
    public TransactionDto save(TransactionDto transactionDto) {
        Transaction entity = TransactionMapper.INSTANCE.dtoToEntity(transactionDto);
        Transaction result = repository.save(entity);
        return TransactionMapper.INSTANCE.entityToDto(result);
    }
}
