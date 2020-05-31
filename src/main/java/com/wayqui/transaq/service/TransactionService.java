package com.wayqui.transaq.service;

import com.wayqui.transaq.dto.TransactionChannel;
import com.wayqui.transaq.dto.TransactionDto;
import com.wayqui.transaq.dto.TransactionStatusDto;
import com.wayqui.transaq.exception.BusinessException;

import java.util.List;

public interface TransactionService {

    /**
     * This method creates a transaction with the information from the input: Reference, IBAN, amount, fee and description.
     * If the reference is not informed the service will generate a random UUID.
     * @param transaction a Dto object to persist on DB.
     * @return the transaction that was persisted on DB.
     * @throws BusinessException in case of any of the following validation error occurs:
     * - If the reference already exists on DB
     * - If the fee is greather than the amount
     * - If is a debit transaction and will leave the total balance for the IBAN bellow zero
     */
    TransactionDto createTransaction(TransactionDto transaction) throws BusinessException;

    /**
     * Obtains the list of transactions associated with an account (IBAN), you can also sort the output
     * based on the amount field.
     * @param account_iban Account IBAN from which we want to retrieve the transactions
     * @param ascending sorting criteria: Ascending, descending or no sorting criteria if not informed
     * @return the list of transactions or an  empty list if there isn't one.
     */
    List<TransactionDto> findTransactions(String account_iban, Boolean ascending);

    /**
     * Obtains the status of a transaction. The list of status are: PENDING, SETTLED, INVALID, FUTURE
     * the service will return one status or other (among other information) based on the reference and
     * the channel that is requesting the transaction information, also the transaction date is considered.
     * @param reference reference id of the transaction
     * @param channel chanel that is requesting the transaction (ATM, INTERNAL, CLIENT)
     * @return a transaction status
     */
    TransactionStatusDto obtainTransactionStatus(String reference, TransactionChannel channel);
}
