package com.wayqui.transaq.service;

import com.wayqui.transaq.api.model.ApiErrorResponse;
import com.wayqui.transaq.dto.TransactionChannel;
import com.wayqui.transaq.dto.TransactionDto;
import com.wayqui.transaq.dto.TransactionStatusDto;
import com.wayqui.transaq.dto.ValidationDto;
import com.wayqui.transaq.exception.BusinessException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class TransactionService {

    /**
     * Function to perform JSR validations on an object. In this case it will validate the input
     * data that comes from the user (through the controller)
     * @param input the input object to be validated
     * @param <I> the data type of the input object
     * @return a list of errors
     */
    public <I> ValidationDto validate(I input) {
        List<String> validationErrors = new ArrayList<>();
        Set<ConstraintViolation<I>> violations = new HashSet<>();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        if (input != null) {
            validator.validate(input).stream().forEach(
                    violation -> validationErrors.add(violation.getMessage()));
        } else {
            validationErrors.add("Input value must not be null");
        }

        return ValidationDto.builder().message("Validation error").errors(validationErrors).build();
    }

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
    public abstract TransactionDto createTransaction(TransactionDto transaction) throws BusinessException;

    /**
     * Obtains the list of transactions associated with an account (IBAN), you can also sort the output
     * based on the amount field.
     * @param account_iban Account IBAN from which we want to retrieve the transactions
     * @param ascending sorting criteria: Ascending, descending or no sorting criteria if not informed
     * @return the list of transactions or an  empty list if there isn't one.
     */
    public abstract List<TransactionDto> findTransactions(String account_iban, Boolean ascending);

    /**
     * Obtains the status of a transaction. The list of status are: PENDING, SETTLED, INVALID, FUTURE
     * the service will return one status or other (among other information) based on the reference and
     * the channel that is requesting the transaction information, also the transaction date is considered.
     * @param reference reference id of the transaction
     * @param channel chanel that is requesting the transaction (ATM, INTERNAL, CLIENT)
     * @return a transaction status
     */
    public abstract TransactionStatusDto obtainTransactionStatus(String reference, TransactionChannel channel);
}
