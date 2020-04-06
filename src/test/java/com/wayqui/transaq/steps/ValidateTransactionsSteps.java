package com.wayqui.transaq.steps;

import com.wayqui.transaq.TransaQApplication;
import com.wayqui.transaq.api.model.TransactionRequest;
import com.wayqui.transaq.api.model.TransactionResponse;
import com.wayqui.transaq.api.model.TransactionStatusResponse;
import io.cucumber.java8.En;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest(classes = TransaQApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ValidateTransactionsSteps implements En {

    private final Logger log = LoggerFactory.getLogger(ValidateTransactionsSteps.class);

    @Autowired
    private TestRestTemplate restTemplate;

    private TransactionRequest unregisteredTransac;

    private TransactionResponse registeredTransac;

    private TransactionStatusResponse transactionStatus;

    private String referenceId;


    public ValidateTransactionsSteps() {

        Given("A transaction that is not stored in our system", () -> {

            referenceId = UUID.randomUUID().toString();

            unregisteredTransac = TransactionRequest.builder()
                    .reference(referenceId)
                    .account_iban("ES9621005463714895928752")
                    .amount(2850.30)
                    .date(Instant.now())
                    .description("Salary for april 2020")
                    .fee(35.5)
                    .build();

        });

        When("^I check the status from (.+) channel$", (String channel) -> {

            Map<String, Object> map = new HashMap<>();
            map.put("reference", referenceId);
            map.put("channel", channel);

            HttpEntity<Map<String, Object>> payload = new HttpEntity<>(map);

            ResponseEntity<TransactionStatusResponse> entity = this.restTemplate.
                    postForEntity("/rest/transaction/status", payload, TransactionStatusResponse.class);

            assertEquals(entity.getStatusCode(), OK);

            transactionStatus = entity.getBody();
        });

        Then("The system returns the status {string}", (String string) -> {
            log.info("Status: "+transactionStatus.getStatus().toString());
            Assert.assertEquals(string, transactionStatus.getStatus());
        });

        Given("^A transaction that is stored in our system$", () -> {
            unregisteredTransac = TransactionRequest.builder()
                    .account_iban("ES9621005463714895928752")
                    .amount(2850.30)
                    .description("Salary for april 2020")
                    .fee(35.5)
                    .build();
        });

        And("^the transaction date is before today$", () -> {
            unregisteredTransac.setDate(Instant.now().minus(1, ChronoUnit.DAYS));

            registeredTransac = this.createTransaction(unregisteredTransac);

            referenceId = registeredTransac.getReference();
        });

        And("^the transaction date is equals to today$", () -> {
            unregisteredTransac.setDate(Instant.now());

            registeredTransac = this.createTransaction(unregisteredTransac);

            referenceId = registeredTransac.getReference();
        });

        And("^the transaction date is greater than today$", () -> {
            unregisteredTransac.setDate(Instant.now().plus(1, ChronoUnit.DAYS));

            registeredTransac = this.createTransaction(unregisteredTransac);

            referenceId = registeredTransac.getReference();
        });

        And("^the amount substracting the fee$", () -> {
            log.info("Amout - fee: "+ transactionStatus.getAmount());
            Assert.assertNull(transactionStatus.getFee());
            Assert.assertEquals(transactionStatus.getAmount(), registeredTransac.getAmount() - registeredTransac.getFee(), 0.001);
        });

        And("^the amount$", () -> {
            Assert.assertNotNull(transactionStatus.getAmount());
            Assert.assertEquals(transactionStatus.getAmount(), registeredTransac.getAmount(), 0.001);
        });

        And("^the fee$", () -> {
            Assert.assertNotNull(transactionStatus.getFee());
            Assert.assertEquals(transactionStatus.getFee(), registeredTransac.getFee(), 0.001);
        });
    }

    private TransactionResponse createTransaction(TransactionRequest transaction) {

        Map<String, Object> map = new HashMap<>();
        map.put("reference", transaction.getReference());
        map.put("account_iban", transaction.getAccount_iban());
        map.put("date", transaction.getDate());
        map.put("amount", transaction.getAmount());
        map.put("fee", transaction.getFee());
        map.put("description", transaction.getDescription());

        HttpEntity<Map<String, Object>> payload = new HttpEntity<>(map);
        ResponseEntity<TransactionResponse> newTransactionResult = null;

        newTransactionResult = this.restTemplate.postForEntity(
                "/rest/transaction", payload, TransactionResponse.class);

        assertEquals(newTransactionResult.getStatusCode(), CREATED);

        return newTransactionResult.getBody();
    }
}
