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
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    TransactionDao transactionDao;

    @Override
    public TransactionDto createTransaction(TransactionDto transaction) throws BusinessException {

        // Validate amount and fee
        this.amountMustBeGreaterThanFee(transaction);

        // Validate and generate reference in case of not being informed
        this.generateTransactionReference(transaction);

        // Validate transaction against IBAN's account balance if it's a debit
        if (transaction.getAmount().compareTo(new BigDecimal("0")) < 0) {
            this.validateIbanAccountBalance(transaction);
        }

        return transactionDao.save(transaction);
    }

    @Override
    public List<TransactionDto> findTransactions(String account_iban, Boolean ascending) {
        return transactionDao.findByIban(account_iban, ascending);
    }

    @Override
    public TransactionStatusDto obtainTransactionStatus(String reference, TransactionChannel channel) {

        List<TransactionDto> transactions = transactionDao.findByReference(reference);
        if (transactions.size() != 1) {
            return TransactionStatusDto.builder().reference(reference).status(TransactionStatus.INVALID).build();
        }
        TransactionDto transaction = transactions.iterator().next();

        TransactionStatus status = this.getTransactionStatus(channel, transaction.getDate());

        BigDecimal calculatedAmount = channel.equals(TransactionChannel.INTERNAL)
                ? transaction.getAmount() : transaction.getAmount().subtract(transaction.getFee());
        BigDecimal calculatedFee = channel.equals(TransactionChannel.INTERNAL)
                ? transaction.getFee() : null;

        return TransactionStatusDto.builder().
                reference(reference).status(status).amount(calculatedAmount).fee(calculatedFee).
                build();
    }

    /**
     * Given a transaction date and a channel, applying some business rules, determine the status of
     * a transaction
     * @param channel that where the request comes from
     * @param transactionDate Date of the transaction
     * @return the status of the transaction: INVALID PENDING, SETTLED, FUTURE
     */
    private TransactionStatus getTransactionStatus(TransactionChannel channel, OffsetDateTime transactionDate) {

        OffsetDateTime transactionDay = transactionDate.truncatedTo(ChronoUnit.DAYS);
        OffsetDateTime today = OffsetDateTime.now().truncatedTo(ChronoUnit.DAYS);

        if (transactionDay.isBefore(today)) return TransactionStatus.SETTLED;
        if (transactionDay.equals(today)) return TransactionStatus.PENDING;
        if (channel.equals(TransactionChannel.ATM)) return TransactionStatus.PENDING;

        return TransactionStatus.FUTURE;
    }

    /**
     * ASSUMPTION: Since it's not mentioned but it's kind of obvious that an amount cannot be inferior to its fee
     * I'm rejecting that request
     * @param transaction the transaction that have the amount and the fee to validate
     * @throws BusinessException a exception if the amount is less than the amount
     */
    private void amountMustBeGreaterThanFee(TransactionDto transaction) throws BusinessException {
        if (transaction.getAmount().abs().compareTo(transaction.getFee()) < 0) {
            throw new BusinessException(
                    "Fee cannot have a greater value than amount",
                    Response.Status.BAD_REQUEST);
        }
    }

    /**
     * ASSUMPTION: Since it's not mentioned what to do in case of a reference already exists
     * I'm rejecting that request
     * @param transaction the transaction whose reference we're validating
     * @throws BusinessException a exception if the reference is already registered
     */
    private void generateTransactionReference(TransactionDto transaction) throws BusinessException {
        String ref = transaction.getReference();
        if (ref != null && !transactionDao.findByReference(ref).isEmpty()) {
            throw new BusinessException(
                    "The transaction with reference id "+ ref + " is already registered",
                    Response.Status.BAD_REQUEST);
        }
        transaction.setReference(ref != null ? ref : UUID.randomUUID().toString());
    }

    /**
     * Validate the account balance from an IBAN and verify if a debit transaction could
     * leave the account with no money in it.
     * @param transaction the transaction we're validating
     * @throws BusinessException a exception if with the input transaction the balance of the acount is zero or less
     */
    private void validateIbanAccountBalance(TransactionDto transaction) throws BusinessException {
        BigDecimal currentBalance = transactionDao.calculateAccountBalance(transaction.getIban());

        BigDecimal balancePlusCurrentTransact = currentBalance.add(
                transaction.getAmount().subtract(transaction.getFee()));

        if (balancePlusCurrentTransact.compareTo(new BigDecimal("0")) < 0) {
            throw new BusinessException(
                    "Transaction forbidden, the current balance for the account is "+currentBalance,
                    Response.Status.BAD_REQUEST);
        }
    }
}