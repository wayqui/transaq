package com.wayqui.transaq.steps;

import com.wayqui.transaq.TransaQApplication;
import com.wayqui.transaq.api.model.ApiErrorResponse;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.validation.constraints.AssertTrue;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

@SpringBootTest(classes = TransaQApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ValidateTransactionsSteps implements En {

    private final Logger log = LoggerFactory.getLogger(ValidateTransactionsSteps.class);

    @Autowired
    private TestRestTemplate restTemplate;

    private TransactionRequest unregisteredTransac;

    private TransactionResponse registeredTransac;

    private TransactionStatusResponse transactionStatus;

    private ResponseEntity<TransactionResponse> createTransacResponse;

    private ResponseEntity<ApiErrorResponse> apiErrorresponse;

    private String referenceId;

    private HttpStatus httpStatusCode;

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
            log.info("I check the status from "+channel+" channel");
            transactionStatus = this.obtainTransactionStatus(referenceId, channel).getBody();
        });

        Then("The system returns the status {string}", (String status) -> {
            log.info("The system returns the status "+status);
            Assert.assertEquals(status, transactionStatus.getStatus());
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
        });

        And("^the transaction date is equals to today$", () -> {
            unregisteredTransac.setDate(Instant.now());
        });

        And("^the transaction date is greater than today$", () -> {
            unregisteredTransac.setDate(Instant.now().plus(1, ChronoUnit.DAYS));
        });

        And("^the amount substracting the fee$", () -> {
            log.info("the amount substracting the fee");
            Assert.assertNull(transactionStatus.getFee());
            Assert.assertEquals(transactionStatus.getAmount(), registeredTransac.getAmount() - registeredTransac.getFee(), 0.001);
        });

        And("^the amount$", () -> {
            log.info("the amount");
            Assert.assertNotNull(transactionStatus.getAmount());
            Assert.assertEquals(transactionStatus.getAmount(), registeredTransac.getAmount(), 0.001);
        });

        And("^the fee$", () -> {
            log.info("the fee");
            Assert.assertNotNull(transactionStatus.getFee());
            Assert.assertEquals(transactionStatus.getFee(), registeredTransac.getFee(), 0.001);
        });

        When("^I persist the transaction in database$", () -> {
            log.info("I persist the transaction in database");
            createTransacResponse = this.createTransaction(unregisteredTransac);
            httpStatusCode = createTransacResponse.getStatusCode();
            registeredTransac = createTransacResponse.getBody();
            referenceId = registeredTransac.getReference();
        });

        When("^I try to persist the transaction in database$", () -> {
            log.info("I try to persist the transaction in database");
            apiErrorresponse = this.errorWhenCreatingTransaction(unregisteredTransac);
            httpStatusCode = apiErrorresponse.getStatusCode();
        });

        And("^the reference transaction is not informed$", () -> {
            log.info("the reference transaction is not informed");
            unregisteredTransac.setReference(null);
        });

        Then("The service returns the HTTP status {string}", (String status) -> {
            log.info("The service returns the HTTP status "+status);
            assertEquals(status, httpStatusCode.getReasonPhrase());
        });

        And("^the transaction reference is informed$", () -> {
            log.info("the transaction reference is informed");
            Assert.assertNotNull(referenceId);
        });

        Given("^the following information for creating a new transaction: (.*), (.*), (.*), (.*) and (.*)$", (String accountiban, String date, String amount, String fee, String description) -> {
            log.info("the following information for creating new transactions: "+accountiban+" - "+date+" - "+amount+" - "+fee+" - "+description);

            unregisteredTransac = TransactionRequest.builder()
                    .account_iban(accountiban)
                    .date(Instant.parse(date))
                    .amount(Double.valueOf(amount))
                    .fee(Double.valueOf(fee))
                    .description(description)
                    .build();

        });
        And("^the transaction reference is not informed$", () -> {
            log.info("the transaction reference is not informed");
            Assert.assertNull(referenceId);
        });

        And("the error message is {string}", (String error) -> {
            log.info("the error message is "+error);
            Assert.assertEquals(error, apiErrorresponse.getBody().getMessage());
        });
        And("with the specific validation error of {string}", (String subError) -> {
            log.info("with the specific validation error of "+subError);
            Assert.assertTrue(apiErrorresponse.getBody().getErrors().stream().anyMatch(error -> error.equalsIgnoreCase(subError)));
        });
    }

    private ResponseEntity<TransactionStatusResponse> obtainTransactionStatus(String referenceId, String channel) {
        Map<String, Object> map = new HashMap<>();
        map.put("reference", referenceId);
        map.put("channel", channel);

        HttpEntity<Map<String, Object>> payload = new HttpEntity<>(map);

        return this.restTemplate.
                postForEntity("/rest/transaction/status", payload, TransactionStatusResponse.class);
    }

    private ResponseEntity<TransactionResponse> createTransaction(TransactionRequest transaction) {

        Map<String, Object> map = new HashMap<>();
        map.put("reference", transaction.getReference());
        map.put("account_iban", transaction.getAccount_iban());
        map.put("date", transaction.getDate());
        map.put("amount", transaction.getAmount());
        map.put("fee", transaction.getFee());
        map.put("description", transaction.getDescription());

        HttpEntity<Map<String, Object>> payload = new HttpEntity<>(map);

        return this.restTemplate.postForEntity(
                "/rest/transaction", payload, TransactionResponse.class);
    }

    private ResponseEntity<ApiErrorResponse> errorWhenCreatingTransaction(TransactionRequest transaction) {

        Map<String, Object> map = new HashMap<>();
        map.put("reference", transaction.getReference());
        map.put("account_iban", transaction.getAccount_iban());
        map.put("date", transaction.getDate());
        map.put("amount", transaction.getAmount());
        map.put("fee", transaction.getFee());
        map.put("description", transaction.getDescription());

        HttpEntity<Map<String, Object>> payload = new HttpEntity<>(map);

        return this.restTemplate.postForEntity(
                "/rest/transaction", payload, ApiErrorResponse.class);
    }
}
