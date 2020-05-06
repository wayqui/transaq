package com.wayqui.transaq.unit.producer;

import com.google.gson.Gson;
import com.wayqui.transaq.conf.kafka.model.TransactionEvent;
import com.wayqui.transaq.conf.kafka.producer.KafkaTransactionProducerImpl;
import com.wayqui.transaq.dto.TransactionDto;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class KafkaTransactionProducerUnitTest {

    @InjectMocks
    KafkaTransactionProducerImpl producer;

    @Mock
    KafkaTemplate<Long, String> kafkaTemplate;

    TransactionEvent message;

    @BeforeEach
    public void setUp() {
        message = TransactionEvent
                .builder()
                .transactionDto(TransactionDto
                        .builder()
                        .reference(UUID.randomUUID().toString())
                        .amount(150d)
                        .date(Instant.now())
                        .description("Testing transaction")
                        .fee(15d)
                        .iban("ES9820385778983000760236")
                        .build())
                .build();
    }

    @Test
    void testingFailDuringMessageSending() {
        // Given
        SettableListenableFuture<SendResult<Long, String>> future = new SettableListenableFuture<>();
        future.setException(new RuntimeException("Error sending message"));

        // when
        when(kafkaTemplate.send(isA(ProducerRecord.class))).thenReturn(future);

        // Then
        assertThrows(Exception.class, () -> producer.sendAsync(message, "transaction-events").get());
    }

    @Test
    void testingSendingMessageCorrectly() throws ExecutionException, InterruptedException {
        // Given
        SettableListenableFuture<SendResult<Long, String>> future = new SettableListenableFuture<>();

        ProducerRecord<Long, String> producerRecord = new ProducerRecord<>("transaction-events", message.getId(), new Gson().toJson(message));

        RecordMetadata metadata = new RecordMetadata(new TopicPartition("transaction-events", 1),
                1, 1, 342, System.currentTimeMillis(), 1, 2);

        SendResult<Long, String> value = new SendResult<>(producerRecord, metadata);
        future.set(value);

        // when
        when(kafkaTemplate.send(isA(ProducerRecord.class))).thenReturn(future);

        // Then
        ListenableFuture<SendResult<Long, String>> futureResult = producer.sendAsync(message, "transaction-events");
        SendResult<Long, String> messageResponse = futureResult.get();

        TransactionEvent transactionEvent = new Gson().fromJson(messageResponse.getProducerRecord().value(), TransactionEvent.class);
        assertEquals(transactionEvent.getTransactionDto(), message.getTransactionDto());
    }
}
