package com.wayqui.transaq.steps;

import com.wayqui.transaq.TransaQApplication;
import com.wayqui.transaq.api.model.ApiErrorResponse;
import com.wayqui.transaq.api.model.TransactionRequest;
import com.wayqui.transaq.api.model.TransactionResponse;
import com.wayqui.transaq.api.model.TransactionStatusResponse;
import com.wayqui.transaq.util.TransactionRequestMapper;
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

import java.text.MessageFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
        this.stepsForValidateTransactions();
        this.stepsForCreateTransactions();
    }

    private void stepsForValidateTransactions() {
        Given("A transaction that is not stored in our system", () -> {
            log.info("A transaction that is not stored in our system");

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
            log.info("A transaction that is stored in our system");
            unregisteredTransac = TransactionRequest.builder()
                    .account_iban("ES9621005463714895928752")
                    .amount(2850.30)
                    .description("Salary for april 2020")
                    .fee(35.5)
                    .build();
        });

        And("^the transaction date is before today$", () -> {
            log.info("the transaction date is before today");
            unregisteredTransac.setDate(Instant.now().minus(1, ChronoUnit.DAYS));
        });

        And("^the transaction date is equals to today$", () -> {
            log.info("the transaction date is equals to today");
            unregisteredTransac.setDate(Instant.now());
        });

        And("^the transaction date is greater than today$", () -> {
            log.info("the transaction date is greater than today");
            unregisteredTransac.setDate(Instant.now().plus(1, ChronoUnit.DAYS));
        });

        And("^the transaction reference is not informed$", () -> {
            log.info("the transaction reference is not informed");
            Assert.assertNull(referenceId);
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
    }

    private void stepsForCreateTransactions() {

        Given("^the following information for creating a new transaction: (.*), (.+), (.+), (.+) and (.+)$",
                (String accountiban, String date, String amount, String fee, String description) -> {

            Object [] data = {accountiban, date, amount, fee, description};
            log.info(MessageFormat.format("the following information for creating new transactions: \n |{0}|{1}|{2}|{3}|{4}|", data));

            unregisteredTransac = TransactionRequest.builder()
                    .account_iban(accountiban)
                    .date(Instant.parse(date))
                    .amount(Double.valueOf(amount))
                    .fee(Double.valueOf(fee))
                    .description(description)
                    .build();
        });

        When("^I persist the transaction in database$", () -> {
            log.info("I persist the transaction in database");
            createTransacResponse = this.createTransaction(unregisteredTransac);
            httpStatusCode = createTransacResponse.getStatusCode();
            registeredTransac = createTransacResponse.getBody();
            referenceId = registeredTransac.getReference();
        });

        Then("The service returns the HTTP status {string}", (String status) -> {
            log.info("The service returns the HTTP status "+status);
            assertEquals(status, httpStatusCode.getReasonPhrase());
        });

        And("^the transaction reference is informed$", () -> {
            log.info("the transaction reference is informed");
            Assert.assertNotNull(referenceId);
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

        And("the error message is {string}", (String error) -> {
            log.info("the error message is "+error);
            Assert.assertEquals(error, apiErrorresponse.getBody().getMessage());
        });

        And("with the specific validation error of {string}", (String subError) -> {
            log.info("with the specific validation error of "+subError);
            Assert.assertTrue(apiErrorresponse.getBody().getErrors().stream().anyMatch(error -> error.equalsIgnoreCase(subError)));
        });
    }

    /**
     * Calling the service using TestRestTemplate client in order to obtain
     * the transaction's status
     * @param referenceId the reference
     * @param channel the channel where the request is coming from
     * @return the response from the service with its payload
     */
    private ResponseEntity<TransactionStatusResponse> obtainTransactionStatus(String referenceId, String channel) {
        HttpEntity<Map<String, Object>> payload = new HttpEntity<>(TransactionRequestMapper.map(referenceId, channel));

        return this.restTemplate.postForEntity("/rest/transaction/status",
                payload, TransactionStatusResponse.class);
    }

    /**
     * Calling the service using TestRestTemplate client in order to create
     * a transaction with the input correctly informed
     * @param transaction the transaction that will be created on the database
     * @return the response with the transaction registered on the database
     */
    private ResponseEntity<TransactionResponse> createTransaction(TransactionRequest transaction) {
        HttpEntity<Map<String, Object>> payload = new HttpEntity<>(TransactionRequestMapper.map(transaction));

        return this.restTemplate.postForEntity("/rest/transaction",
                payload, TransactionResponse.class);
    }

    /**
     * Calling the service using TestRestTemplate client in order to create
     * a transaction but with the request with incorrect, missing or
     * information already registered
     * @param transaction the transaction that won't be created on the database
     * @return the response with the error that caused the transaction was not created
     */
    private ResponseEntity<ApiErrorResponse> errorWhenCreatingTransaction(TransactionRequest transaction) {
        HttpEntity<Map<String, Object>> payload = new HttpEntity<>(TransactionRequestMapper.map(transaction));

        return this.restTemplate.postForEntity("/rest/transaction",
                payload, ApiErrorResponse.class);
    }
}
