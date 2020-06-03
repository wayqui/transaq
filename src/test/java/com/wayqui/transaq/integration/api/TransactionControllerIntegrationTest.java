package com.wayqui.transaq.integration.api;

import com.wayqui.avro.TransactionAvro;
import com.wayqui.transaq.TransaQApplication;
import com.wayqui.transaq.api.model.AuthenticateRequest;
import com.wayqui.transaq.api.model.AuthenticateResponse;
import com.wayqui.transaq.api.model.TransactionRequest;
import com.wayqui.transaq.api.model.TransactionResponse;
import com.wayqui.transaq.avro.CustomKafkaAvroDeserializer;
import com.wayqui.transaq.conf.security.SecurityConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Conversions;
import org.apache.avro.LogicalTypes;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.junit.Assert;
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

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;

import static com.wayqui.transaq.conf.security.SecurityConstants.AUTH_LOGIN_URL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@SpringBootTest(classes = {TransaQApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@EmbeddedKafka(
        topics = {"transaction-events"},
        partitions = 1,
        brokerProperties={
                "log.dir=out/embedded-kafka"
        })
public class TransactionControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EmbeddedKafkaBroker kafkaBroker;

    private Consumer<Long, TransactionAvro> consumer;
    private TransactionRequest transactionRequest;

    private String validToken;

    @BeforeEach
    public void setUp_Authentication_and_Create_Consumer() {
        this.setUpAuthentication();
        this.setUpConsumer();
        this.createRequest();
    }

    private void setUpConsumer() {
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("group", "true", kafkaBroker));
        configs.put("key.deserializer", LongDeserializer.class);
        configs.put("value.deserializer", CustomKafkaAvroDeserializer.class);
        configs.put("schema.registry.url", "not-used");
        configs.put("specific.avro.reader", "true");

        consumer = new DefaultKafkaConsumerFactory<Long, TransactionAvro>(configs).createConsumer();

        kafkaBroker.consumeFromAllEmbeddedTopics(consumer);
    }

    private void createRequest() {
        transactionRequest = TransactionRequest.builder()
                .reference(UUID.randomUUID().toString())
                .amount(new BigDecimal("2850.30"))
                .date(OffsetDateTime.now())
                .description("Testing transaction")
                .fee(new BigDecimal("15.30"))
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
        ResponseEntity<TransactionResponse> response = restTemplate
                .exchange("/rest/transaction", HttpMethod.POST, request, TransactionResponse.class);

        ConsumerRecord<Long, TransactionAvro> record = KafkaTestUtils.getSingleRecord(consumer, "transaction-events");

        TransactionAvro resultMessage = record.value();

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(record.value());

        Conversions.DecimalConversion DECIMAL_CONVERTER = new Conversions.DecimalConversion();
        LogicalTypes.Decimal decimalType = LogicalTypes.decimal(6, 2);

        BigDecimal amount = DECIMAL_CONVERTER.fromBytes(resultMessage.getAmount(), null, decimalType);
        BigDecimal fee = DECIMAL_CONVERTER.fromBytes(resultMessage.getFee(), null, decimalType);

        Assert.assertEquals(transactionRequest.getAccount_iban(), resultMessage.getIban());
        Assert.assertEquals(transactionRequest.getDescription(), resultMessage.getDescription());
        Assert.assertNotNull(resultMessage.getReference());
        Assert.assertEquals(transactionRequest.getAmount(), amount);
        Assert.assertEquals(transactionRequest.getFee(), fee);
        Assert.assertEquals(transactionRequest.getDate().toInstant().toEpochMilli(), resultMessage.getDate().longValue());
    }

    @AfterEach
    public void shutDown() {
        consumer.close();
    }

}
