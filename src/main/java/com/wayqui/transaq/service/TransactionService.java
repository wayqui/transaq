package com.wayqui.transaq.service;

import com.wayqui.transaq.dto.TransactionChannel;
import com.wayqui.transaq.dto.TransactionDto;
import com.wayqui.transaq.dto.TransactionStatusDto;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.*;

public abstract class TransactionService {

    public <I> List<String> validate(I input) {
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

        return validationErrors;
    }

    public abstract TransactionDto createTransaction(TransactionDto transaction);

    public abstract List<TransactionDto> findTransactions(String account_iban, Boolean ascending);

    public abstract TransactionStatusDto obtainTransactionStatus(String reference, TransactionChannel channel);
}
