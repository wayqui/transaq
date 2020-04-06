package com.wayqui.transaq.service;

import com.wayqui.transaq.dto.TransactionChannel;
import com.wayqui.transaq.dto.TransactionDto;
import com.wayqui.transaq.dto.TransactionStatus;
import com.wayqui.transaq.dto.TransactionStatusDto;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl extends TransactionService {

    private static List<TransactionDto> transactions = new ArrayList<>(Arrays.asList(
            TransactionDto.builder()
                    .reference(UUID.randomUUID().toString())
                    .account_iban("ES9621005463714895928752")
                    .amount(2850.30).date(Instant.now())
                    .description("Salary for april 2020")
                    .fee(-35.5).build(),
            TransactionDto.builder()
                    .reference(UUID.randomUUID().toString())
                    .account_iban("ES9621005463714895928752")
                    .amount(-153.00).date(Instant.now())
                    .description("Water bill")
                    .fee(8.7).build(),
            TransactionDto.builder()
                    .reference(UUID.randomUUID().toString())
                    .account_iban("ES9321006241817383882283")
                    .amount(-60.0).date(Instant.now())
                    .description("Rent for april 2020")
                    .fee(3.5).build(),
            TransactionDto.builder()
                    .reference(UUID.randomUUID().toString())
                    .account_iban("ES2530044816479877682687")
                    .amount(-134.43).date(Instant.now())
                    .description("Internet receipt")
                    .fee(3.5).build(),
            TransactionDto.builder()
                    .reference(UUID.randomUUID().toString())
                    .account_iban("ES1821003151618627798236")
                    .amount(-77.80).date(Instant.now())
                    .description("Car insurance")
                    .fee(13.0).build()
    ));

    @Override
    public TransactionDto createTransaction(TransactionDto transaction) {

        Optional<TransactionDto> existingTransaction = Optional.empty();

        if (transaction.getReference() != null) {
            existingTransaction = transactions.stream()
                    .filter(t -> t.getReference().equalsIgnoreCase(transaction.getReference())).findFirst();
        } else {
            transaction.setReference(UUID.randomUUID().toString());
        }

        if (!existingTransaction.isPresent()) {
            transactions.add(transaction);
            return transaction;
        } else {
            // ASSUMPTION: Since it's not mentioned what to do in case of the transaction reference exist
            // I'm rejecting that request

            // FIXME improve error handling
            throw new RuntimeException("The transaction with reference id "+ transaction.getReference() + " is already registered");
        }
    }

    @Override
    public List<TransactionDto> findTransactions(String account_iban, Boolean ascending) {
        List<TransactionDto> filtered = transactions.stream()
                .filter(t -> t.getAccount_iban().equalsIgnoreCase(account_iban))
                .collect(Collectors.toList());

        if (ascending != null) {
            if (ascending) {
                return filtered.stream()
                        .sorted(Comparator.comparingDouble(TransactionDto::getAmount))
                        .collect(Collectors.toList());
            } else {
                return filtered.stream()
                        .sorted(Comparator.comparingDouble(TransactionDto::getAmount).reversed())
                        .collect(Collectors.toList());
            }
        }

        return filtered;
    }

    @Override
    public TransactionStatusDto obtainTransactionStatus(String reference, TransactionChannel channel) {

        TransactionStatusDto status = TransactionStatusDto.builder().reference(reference).status(TransactionStatus.INVALID).build();
        Optional<TransactionDto> result = transactions.stream().filter(t -> t.getReference().equalsIgnoreCase(reference)).findFirst();
        if (result.isPresent()) {
            TransactionDto transaction = result.get();

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
