package com.wayqui.transaq.service;

import com.wayqui.transaq.api.model.TransactionResponse;
import com.wayqui.transaq.dto.*;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.*;

@Service
public class TransactionServiceImpl implements TransactionService {

    private static final List<TransactionDto> transactions = Arrays.asList(
            TransactionDto.builder()
                    .reference(UUID.randomUUID().toString())
                    .account_iban("ES9621005463714895928752")
                    .amount(2850.30).date(new Date())
                    .description("Salary for april 2020")
                    .fee(-35.5).build(),
            TransactionDto.builder()
                    .reference(UUID.randomUUID().toString())
                    .account_iban("ES9621005463714895928752")
                    .amount(-153.00).date(new Date())
                    .description("Water bill")
                    .fee(8.7).build(),
            TransactionDto.builder()
                    .reference(UUID.randomUUID().toString())
                    .account_iban("ES9321006241817383882283")
                    .amount(-60.0).date(new Date())
                    .description("Rent for april 2020")
                    .fee(3.5).build(),
            TransactionDto.builder()
                    .reference(UUID.randomUUID().toString())
                    .account_iban("ES2530044816479877682687")
                    .amount(-134.43).date(new Date())
                    .description("Internet receipt")
                    .fee(3.5).build(),
            TransactionDto.builder()
                    .reference(UUID.randomUUID().toString())
                    .account_iban("ES1821003151618627798236")
                    .amount(-77.80).date(new Date())
                    .description("Car insurance")
                    .fee(13.0).build()
    );

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
    public TransactionResultDto createTransaction(TransactionDto transaction) {

        Optional<TransactionDto> found = transactions.stream()
                .filter(t -> t.getReference().equalsIgnoreCase(transaction.getReference())).findFirst();

        if (!found.isPresent()) {
            transactions.add(transaction);
        }

        return null;
    }

    @Override
    public TransactionResponse findTransaction(String account_iban, Boolean ascending) {
        return null;
    }

    @Override
    public Optional<TransactionStatusDto> obtainTransactionStatus(String reference, TransactionChannel channel) {

        TransactionStatusDto status = new TransactionStatusDto(reference, TransactionStatus.INVALID);
        Optional<TransactionDto> result = transactions.stream().filter(t -> t.getReference().equalsIgnoreCase(reference)).findFirst();
        if (result.isPresent()) {
            // TODO implement login when is present
        }

        return Optional.of(status);
    }
}
