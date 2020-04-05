package com.wayqui.transaq.service;

import com.wayqui.transaq.api.model.TransactionRequest;
import com.wayqui.transaq.api.model.TransactionResponse;
import com.wayqui.transaq.api.model.TransactionStatusRequest;
import com.wayqui.transaq.api.model.TransactionStatusResponse;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Override
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

    @Override
    public TransactionResponse createTransaction(TransactionRequest transaction) {
        return null;
    }

    @Override
    public TransactionResponse findTransaction(String account_iban, Boolean ascending) {
        return null;
    }

    @Override
    public TransactionStatusResponse obtainTransactionStatus(TransactionStatusRequest transactionStatus) {
        return null;
    }


}
