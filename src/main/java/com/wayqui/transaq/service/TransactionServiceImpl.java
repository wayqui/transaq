package com.wayqui.transaq.service;

import com.wayqui.transaq.dao.TransactionDao;
import com.wayqui.transaq.dto.TransactionChannel;
import com.wayqui.transaq.dto.TransactionDto;
import com.wayqui.transaq.dto.TransactionStatus;
import com.wayqui.transaq.dto.TransactionStatusDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl extends TransactionService {

    @Autowired
    TransactionDao transactionDao;

    @Override
    public TransactionDto createTransaction(TransactionDto transaction) {

        List<TransactionDto> transactionsByReference = new ArrayList<>();
        if (transaction.getReference() != null) {
            transactionsByReference = transactionDao.findByReference(transaction.getReference());
        } else {
            transaction.setReference(UUID.randomUUID().toString());
        }

        if (transactionsByReference.isEmpty()) {

            // TODO Verify if the total amount of the account is greater than zero with that transactioin
            return transactionDao.save(transaction);
        } else {
            // ASSUMPTION: Since it's not mentioned what to do in case of the transaction reference exist
            // I'm rejecting that request

            // FIXME improve error handling
            throw new RuntimeException("The transaction with reference id "+ transaction.getReference() + " is already registered");
        }
    }

    @Override
    public List<TransactionDto> findTransactions(String account_iban, Boolean ascending) {

        List<TransactionDto> transactionsByIban = transactionDao.findByIban(account_iban);

        // FIXME Sort in the query

        if (ascending != null) {
            if (ascending) {
                return transactionsByIban.stream()
                        .sorted(Comparator.comparingDouble(TransactionDto::getAmount))
                        .collect(Collectors.toList());
            } else {
                return transactionsByIban.stream()
                        .sorted(Comparator.comparingDouble(TransactionDto::getAmount).reversed())
                        .collect(Collectors.toList());
            }
        }

        return transactionsByIban;
    }

    @Override
    public TransactionStatusDto obtainTransactionStatus(String reference, TransactionChannel channel) {

        List<TransactionDto> transactionsByReference = transactionDao.findByReference(reference);

        TransactionStatusDto status = TransactionStatusDto.builder()
                .reference(reference).status(TransactionStatus.INVALID).build();

        if (transactionsByReference.size() == 1) {
            TransactionDto transaction = transactionsByReference.iterator().next();

            Instant transactionDate = transaction.getDate().truncatedTo(ChronoUnit.DAYS);
            Instant today = Instant.now().truncatedTo(ChronoUnit.DAYS);

            if (channel.equals(TransactionChannel.INTERNAL)) {
                status.setAmount(transaction.getAmount());
                status.setFee(transaction.getFee());
            } else {
                status.setAmount(transaction.getAmount() - transaction.getFee());
            }

            if (transactionDate.isBefore(today)) {
                status.setStatus(TransactionStatus.SETTLED);
            } else if (transactionDate.isAfter(today)) {
                status.setStatus(TransactionStatus.FUTURE);
                if (channel.equals(TransactionChannel.ATM)) {
                    status.setStatus(TransactionStatus.PENDING);
                }
            } else {
                status.setStatus(TransactionStatus.PENDING);
            }
        }

        return status;
    }
}
