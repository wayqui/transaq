package com.wayqui.transaq.steps;

import com.wayqui.transaq.dto.TransactionChannel;
import com.wayqui.transaq.dto.TransactionDto;
import com.wayqui.transaq.dto.TransactionResultDto;
import com.wayqui.transaq.dto.TransactionStatusDto;
import com.wayqui.transaq.service.TransactionService;
import io.cucumber.java8.En;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@SpringBootTest
public class ValidateTransactionsSteps implements En {

    private final Logger log = LoggerFactory.getLogger(ValidateTransactionsSteps.class);

    @Autowired
    private TransactionService transactionService;

    private TransactionDto unregisteredTransac;

    private TransactionDto registeredTransac;

    private Optional<TransactionStatusDto> result;

    public ValidateTransactionsSteps() {

        Given("A transaction that is not stored in our system", () -> {
            unregisteredTransac = TransactionDto.builder()
                    .account_iban("ES9621005463714895928752")
                    .amount(2850.30).date(Instant.now())
                    .description("Salary for april 2020")
                    .fee(-35.5).build();
        });

        When("^I check the status from (.+) channel$", (String channel) -> {
            result = transactionService.obtainTransactionStatus(
                    unregisteredTransac.getReference(), TransactionChannel.valueOf(channel));
        });

        Then("The system returns the status {string}", (String string) -> {
            log.info("Status: "+result.get().getStatus().toString());
            Assert.assertTrue(result.isPresent());
            Assert.assertEquals(string, result.get().getStatus().toString());
        });


        Given("^A transaction that is stored in our system$", () -> {
            unregisteredTransac = TransactionDto.builder()
                    .account_iban("ES9621005463714895928752")
                    .amount(2850.30)
                    .description("Salary for april 2020")
                    .fee(-35.5).build();
        });

        And("^the transaction date is before today$", () -> {
            unregisteredTransac.setDate(Instant.now().minus(1, ChronoUnit.DAYS));
            TransactionResultDto result = transactionService.createTransaction(unregisteredTransac);
            registeredTransac = result.getTransactionDto().get();
        });

        And("^the transaction date is equals to today$", () -> {
            unregisteredTransac.setDate(Instant.now());
            TransactionResultDto result = transactionService.createTransaction(unregisteredTransac);
            registeredTransac = result.getTransactionDto().get();
        });

        And("^the transaction date is greater than today$", () -> {
            unregisteredTransac.setDate(Instant.now().plus(1, ChronoUnit.DAYS));
            TransactionResultDto result = transactionService.createTransaction(unregisteredTransac);
            registeredTransac = result.getTransactionDto().get();
        });

        And("^the amount substracting the fee$", () -> {
            log.info("Amout - fee: "+ result.get().getAmount());
            Assert.assertNull(result.get().getFee());
            Assert.assertEquals(result.get().getAmount(), registeredTransac.getAmount() - registeredTransac.getFee(), 0.001);
        });

        And("^the amount$", () -> {
            Assert.assertNotNull(result.get().getAmount());
            Assert.assertEquals(result.get().getAmount(), registeredTransac.getAmount(), 0.001);
        });

        And("^the fee$", () -> {
            Assert.assertNotNull(result.get().getFee());
            Assert.assertEquals(result.get().getFee(), registeredTransac.getFee(), 0.001);
        });


    }
}
