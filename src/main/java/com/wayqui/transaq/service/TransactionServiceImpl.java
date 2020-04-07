package com.wayqui.transaq.service;

import com.wayqui.transaq.dao.TransactionDao;
import com.wayqui.transaq.dto.TransactionChannel;
import com.wayqui.transaq.dto.TransactionDto;
import com.wayqui.transaq.dto.TransactionStatus;
import com.wayqui.transaq.dto.TransactionStatusDto;
import com.wayqui.transaq.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
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

        if (Math.abs(transaction.getAmount()) < transaction.getFee()) {
            // ASSUMPTION: Since it's not mentioned but it's kind of obvious that an ammout cannot be inferior to its fee
            // I'm rejecting that request

            throw new BusinessException("Fee cannot have a greater value than amount", Response.Status.BAD_REQUEST);
        }

        List<TransactionDto> transactionsByReference = new ArrayList<>();
        if (transaction.getReference() != null) {
            transactionsByReference = transactionDao.findByReference(transaction.getReference());
        } else {
            transaction.setReference(UUID.randomUUID().toString());
        }

        if (!transactionsByReference.isEmpty()) {
            // ASSUMPTION: Since it's not mentioned what to do in case of the transaction reference exist
            // I'm rejecting that request

            throw new BusinessException("The transaction with reference id "+ transaction.getReference() + " is already registered", Response.Status.BAD_REQUEST);
        }

        if (transaction.getAmount() < 0) { // It's a debit!

            double currentBalance = this.calculateTotalAccountBalance(transaction.getIban());

            double balanceWithTransaction = currentBalance + transaction.getAmount() - transaction.getFee();

            if (balanceWithTransaction >= 0) {
                return transactionDao.save(transaction);
            } else {
                throw new BusinessException("Debit transaction not allowed since the current balance for the account is "+currentBalance, Response.Status.BAD_REQUEST);
            }
        }

        return transactionDao.save(transaction);
    }

    @Override
    public List<TransactionDto> findTransactions(String account_iban) {
        return this.findTransactions(account_iban, null);
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

    private double calculateTotalAccountBalance(String iban) {
        List<TransactionDto> transactionsByIban = this.findTransactions(iban);

        return transactionsByIban.stream().mapToDouble(v -> v.getAmount() - v.getFee()).sum();
    }
}
