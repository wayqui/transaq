package com.wayqui.transaq.steps;

import com.wayqui.transaq.dto.TransactionChannel;
import com.wayqui.transaq.dto.TransactionDto;
import com.wayqui.transaq.dto.TransactionStatusDto;
import com.wayqui.transaq.service.TransactionService;
import io.cucumber.java8.En;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
public class ValidateTransactionsSteps implements En {

    private final Logger log = LoggerFactory.getLogger(ValidateTransactionsSteps.class);

    @Autowired
    private TransactionService transactionService;

    private TransactionDto unregisteredTransac;

    private Optional<TransactionStatusDto> result;

    public ValidateTransactionsSteps() {

        /*
         * Scenario: Verify a nonexistent transaction
         */
        Given("A transaction that is not stored in our system", () -> {
            unregisteredTransac = TransactionDto.builder()
                    .reference(UUID.randomUUID().toString())
                    .account_iban("ES9621005463714895928752")
                    .amount(2850.30).date(new Date())
                    .description("Salary for april 2020")
                    .fee(-35.5).build();
        });

        When("^I check the status from (.+)$", (String channel) -> {

            result = transactionService.obtainTransactionStatus(
                    unregisteredTransac.getReference(), TransactionChannel.valueOf(channel));
        });

        Then("The system returns the status {string}", (String string) -> {
            log.info("Status: "+result.get().getStatus().toString());
            Assert.assertTrue(result.isPresent());
            Assert.assertEquals(string, result.get().getStatus().toString());
        });


    }
}
