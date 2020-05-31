package com.wayqui.transaq.integration.api;

import com.google.gson.Gson;
import com.wayqui.transaq.api.model.AuthenticateRequest;
import com.wayqui.transaq.api.model.AuthenticateResponse;
import com.wayqui.transaq.api.model.TransactionRequest;
import com.wayqui.transaq.conf.kafka.model.TransactionEvent;
import com.wayqui.transaq.conf.security.SecurityConstants;
import com.wayqui.transaq.dto.TransactionDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;

import static com.wayqui.transaq.conf.security.SecurityConstants.AUTH_LOGIN_URL;
import static org.junit.Assert.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@EmbeddedKafka(
        topics = {"transaction-events"},
        partitions = 3,
        brokerProperties={
                "log.dir=out/embedded-kafka"
        })
@TestPropertySource(
        properties = {
                "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}"
        })
public class TransactionControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EmbeddedKafkaBroker kafkaBroker;

    private Consumer<Long, String> consumer;
    private TransactionRequest transactionRequest;
    private TransactionDto transactionResponse;

    private String validToken;

    @BeforeEach
    public void setUp_Authentication_and_Create_Consumer() {

        setUpAuthentication();

        setUpConsumer();

        createRequest();
    }

    private void setUpConsumer() {
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("group", "true", kafkaBroker));
        consumer = new DefaultKafkaConsumerFactory<>(configs,
                new LongDeserializer(), new StringDeserializer())
                .createConsumer();
        kafkaBroker.consumeFromAllEmbeddedTopics(consumer);
    }

    private void createRequest() {
        transactionRequest = TransactionRequest.builder()
                .reference(UUID.randomUUID().toString())
                .amount(BigDecimal.valueOf(150d))
                .date(OffsetDateTime.now())
                .description("Testing transaction")
                .fee(BigDecimal.valueOf(15d))
                .account_iban("ES9820385778983000760236")
                .build();
    }

    private void setUpAuthentication() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        AuthenticateRequest authRequest = new AuthenticateRequest("appuser", "pwdappuser");

        HttpEntity<AuthenticateRequest> requestEntity = new HttpEntity<>(authRequest, headers);

        ResponseEntity<AuthenticateResponse> response = restTemplate
                .withBasicAuth("appuser", "pwdappuser")
                .exchange(AUTH_LOGIN_URL, HttpMethod.GET, requestEntity, AuthenticateResponse.class);

        validToken = new String(response.getHeaders().getFirst(SecurityConstants.TOKEN_HEADER).getBytes()).substring(7);
    }

    @Test
    @Timeout(10)
    public void testingCreateTransaction() {

        // Given
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.set(SecurityConstants.TOKEN_HEADER, SecurityConstants.TOKEN_PREFIX + validToken);

        HttpEntity<TransactionRequest> request = new HttpEntity<>(transactionRequest, headers);

        // When
        ResponseEntity<TransactionEvent> response = restTemplate
                .exchange("/rest/transaction", HttpMethod.POST, request, TransactionEvent.class);

        ConsumerRecord<Long, String> record = KafkaTestUtils.getSingleRecord(consumer, "transaction-events");
        transactionResponse = new Gson().fromJson(record.value(), TransactionDto.class);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        assertEquals(transactionRequest.getAccount_iban(), transactionResponse.getIban());
        assertEquals(transactionRequest.getDescription(), transactionResponse.getDescription());
        assertEquals(transactionRequest.getAmount(), transactionResponse.getAmount());
        assertEquals(transactionRequest.getDate(), transactionResponse.getDate());
        assertEquals(transactionRequest.getFee(), transactionResponse.getFee());
        assertEquals(transactionRequest.getReference(), transactionResponse.getReference());
    }

    @AfterEach
    public void shutDown() {
        consumer.close();
    }

}
