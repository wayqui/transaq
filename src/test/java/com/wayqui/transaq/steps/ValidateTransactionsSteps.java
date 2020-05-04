package com.wayqui.transaq.steps;

import com.wayqui.transaq.TransaQApplication;
import com.wayqui.transaq.api.model.*;
import com.wayqui.transaq.conf.security.JWTTokenHandler;
import com.wayqui.transaq.conf.security.SecurityConstants;
import com.wayqui.transaq.dao.UserRepository;
import com.wayqui.transaq.entity.AppUser;
import com.wayqui.transaq.service.UserService;
import io.cucumber.java8.En;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.text.MessageFormat;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.Assert.*;

@SpringBootTest(classes = TransaQApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ValidateTransactionsSteps implements En {


    private static final String TRANSAC_STATUS = "/rest/transaction/status";
    private static final String CREATE_TRANSAC = "/rest/transaction";

    private final Logger log = LoggerFactory.getLogger(ValidateTransactionsSteps.class);

    @Autowired
    private TestRestTemplate restTemplate;

    //TODO remove repository call and implement save method at service level
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private UserService userService;

    @Autowired
    private JWTTokenHandler tokenHandler;

    private String username;
    private String password;

    private TransactionRequest unregisteredTransac;
    private TransactionResponse registeredTransac;
    private ResponseEntity<ApiErrorResponse> apiErrorresponse;
    private String referenceId;
    private HttpStatus httpStatusCode;
    private TransactionStatusResponse transactionStatus;

    private AppUser registeredAppUser;
    private String validToken;

    public ValidateTransactionsSteps() {
        this.stepsForAuthentication();
        this.stepsForValidateTransactions();
        this.stepsForCreateTransactions();

    }

    private void stepsForAuthentication() {

        Given("^A user registered in our system$", () -> {
            this.username = UUID.randomUUID().toString();
            this.password = UUID.randomUUID().toString();

            log.info("Registering user "+this.username+" and password "+this.password);
            AppUser unregisteredAppUser = new AppUser();
            unregisteredAppUser.setUsername(this.username);
            unregisteredAppUser.setPassword(encoder.encode(this.password));
            registeredAppUser = userRepository.save(unregisteredAppUser);
            assertNotNull(registeredAppUser);
            assertNotNull(registeredAppUser.getId());
            assertEquals(this.username, registeredAppUser.getUsername());
        });

        But("^the user logs in$", () -> {
            ResponseEntity<AuthenticateResponse> response = this.authenticateUser(this.username, this.password);
            httpStatusCode = response.getStatusCode();
            validToken = new String(response.getHeaders().getFirst(SecurityConstants.TOKEN_HEADER).getBytes()).substring(7);
        });

        And("^a valid JWT token is generated$", () -> {
            log.info(validToken);
            final UserDetails userDetails = userService.loadUserByUsername(username);
            assertTrue(tokenHandler.validateToken(validToken, userDetails));
        });
    }

    private void stepsForValidateTransactions() {
        Given("A transaction that is not stored in our system", () -> {
            log.info("A transaction that is not stored in our system");

            referenceId = UUID.randomUUID().toString();
            unregisteredTransac = TransactionRequest.builder()
                    .reference(referenceId)
                    .account_iban("ES9621005463714895928752")
                    .amount(2850.30)
                    .date(OffsetDateTime.now())
                    .description("Salary for april 2020")
                    .fee(35.5)
                    .build();
        });

        When("^I check the status from (.+) channel$", (String channel) -> {
            log.info("I check the status from "+channel+" channel");
            ResponseEntity<TransactionStatusResponse> response = this.obtainTransactionStatus(referenceId, channel);
            Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
            transactionStatus = response.getBody();
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
            unregisteredTransac.setDate(OffsetDateTime.now().minus(1, ChronoUnit.DAYS));
        });

        And("^the transaction date is equals to today$", () -> {
            log.info("the transaction date is equals to today");
            unregisteredTransac.setDate(OffsetDateTime.now());
        });

        And("^the transaction date is greater than today$", () -> {
            log.info("the transaction date is greater than today");
            unregisteredTransac.setDate(OffsetDateTime.now().plus(1, ChronoUnit.DAYS));
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
                    .date(OffsetDateTime.parse(date))
                    .amount(Double.valueOf(amount))
                    .fee(Double.valueOf(fee))
                    .description(description)
                    .build();
        });

        When("^I persist the transaction in database$", () -> {
            log.info("I persist the transaction in database");
            ResponseEntity<TransactionResponse> response = this.createTransaction(unregisteredTransac);
            httpStatusCode = response.getStatusCode();
            registeredTransac = response.getBody();
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
     * Calling the service using TestRestTemplate to authenticate a user and, if correct,
     * a JWT token is generated. The user must be registered on the system (TR_USER table)
     * @param username the user trying to access the application
     * @param password the password
     * @return the response with the JWT token informed
     */
    private ResponseEntity<AuthenticateResponse> authenticateUser(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        AuthenticateRequest authRequest = new AuthenticateRequest(username, password);

        HttpEntity<AuthenticateRequest> requestEntity = new HttpEntity<>(authRequest, headers);

        return restTemplate.withBasicAuth(username, password)
                .postForEntity(SecurityConstants.AUTH_LOGIN_URL, requestEntity, AuthenticateResponse.class);
    }

    /**
     * Calling the service using TestRestTemplate client in order to obtain
     * the transaction's status
     * @param referenceId the reference
     * @param channel the channel where the request is coming from
     * @return the response from the service with its payload
     */
    private ResponseEntity<TransactionStatusResponse> obtainTransactionStatus(String referenceId, String channel) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.set(SecurityConstants.TOKEN_HEADER, SecurityConstants.TOKEN_PREFIX + validToken);

        TransactionStatusRequest request = TransactionStatusRequest
                .builder()
                .reference(referenceId)
                .channel(channel)
                .build();

        HttpEntity<TransactionStatusRequest> requestEntity = new HttpEntity<>(request, headers);

        return restTemplate.postForEntity(TRANSAC_STATUS, requestEntity, TransactionStatusResponse.class);
    }

    /**
     * Calling the service using TestRestTemplate client in order to create
     * a transaction with the input correctly informed
     * @param transaction the transaction that will be created on the database
     * @return the response with the transaction registered on the database
     */
    private ResponseEntity<TransactionResponse> createTransaction(TransactionRequest transaction) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.set(SecurityConstants.TOKEN_HEADER, SecurityConstants.TOKEN_PREFIX + validToken);

        HttpEntity<TransactionRequest> requestEntity = new HttpEntity<>(transaction, headers);

        return restTemplate
                .postForEntity(CREATE_TRANSAC, requestEntity, TransactionResponse.class);
    }

    /**
     * Calling the service using TestRestTemplate client in order to create
     * a transaction but with the request with incorrect, missing or
     * information already registered
     * @param transaction the transaction that won't be created on the database
     * @return the response with the error that caused the transaction was not created
     */
    private ResponseEntity<ApiErrorResponse> errorWhenCreatingTransaction(TransactionRequest transaction) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.set(SecurityConstants.TOKEN_HEADER, SecurityConstants.TOKEN_PREFIX + validToken);

        HttpEntity<TransactionRequest> requestEntity = new HttpEntity<>(transaction, headers);

        return restTemplate
                .postForEntity(CREATE_TRANSAC, requestEntity, ApiErrorResponse.class);
    }
}
