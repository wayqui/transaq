package com.wayqui.transaq.unit.producer;

import com.wayqui.avro.TransactionAvro;
import com.wayqui.transaq.conf.kafka.model.TransactionEvent;
import com.wayqui.transaq.conf.kafka.producer.KafkaTransactionProducerImpl;
import com.wayqui.transaq.dto.TransactionDto;
import org.apache.avro.Conversions;
import org.apache.avro.LogicalTypes;
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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class KafkaTransactionProducerUnitTest {

    @InjectMocks
    KafkaTransactionProducerImpl producer;

    @Mock
    KafkaTemplate<Long, String> kafkaTemplate;

    TransactionEvent transactionEvent;

    TransactionAvro transactionAvro;

    @BeforeEach
    public void setUp() {
        transactionEvent = TransactionEvent
                .builder()
                .transactionDto(TransactionDto
                        .builder()
                        .reference(UUID.randomUUID().toString())
                        .amount(new BigDecimal(new BigInteger("50"), 2))
                        .date(Instant.now())
                        .description("Testing transaction")
                        .fee(new BigDecimal(new BigInteger("30"), 2))
                        .iban("ES9820385778983000760236")
                        .build())
                .build();

        TransactionDto dto = transactionEvent.getTransactionDto();

        ByteBuffer feeBuff = ByteBuffer.wrap(dto.getFee().unscaledValue().toByteArray());
        ByteBuffer amountBuff = ByteBuffer.wrap(dto.getAmount().unscaledValue().toByteArray());

        transactionAvro = TransactionAvro.newBuilder()
                .setFee(feeBuff)
                .setAmount(amountBuff)
                .setReference(dto.getReference())
                .setIban(dto.getIban())
                .setDescription(dto.getDescription())
                .setDate(dto.getDate().toEpochMilli())
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
        assertThrows(Exception.class, () -> producer.sendAsync(transactionEvent, "transaction-events").get());
    }

    @Test
    void testingSendingMessageCorrectly() throws ExecutionException, InterruptedException {
        // Given
        SettableListenableFuture<SendResult<Long, TransactionAvro>> future = new SettableListenableFuture<>();

        ProducerRecord<Long, TransactionAvro> producerRecord =
                new ProducerRecord<>("transaction-events", transactionEvent.getId(), transactionAvro);

        RecordMetadata metadata = new RecordMetadata(new TopicPartition("transaction-events", 1),
                1, 1, 342, System.currentTimeMillis(), 1, 2);

        SendResult<Long, TransactionAvro> value = new SendResult<>(producerRecord, metadata);
        future.set(value);

        // when
        when(kafkaTemplate.send(isA(ProducerRecord.class))).thenReturn(future);

        // Then
        ListenableFuture<SendResult<Long, TransactionAvro>> futureResult = producer.sendAsync(transactionEvent, "transaction-events");
        SendResult<Long, TransactionAvro> messageResponse = futureResult.get();

        TransactionAvro resultMessage = messageResponse.getProducerRecord().value();

        Conversions.DecimalConversion DECIMAL_CONVERTER = new Conversions.DecimalConversion();
        LogicalTypes.Decimal decimaltype = LogicalTypes.decimal(6, 2);

        BigDecimal amount = DECIMAL_CONVERTER.fromBytes(resultMessage.getAmount(), null, decimaltype);
        BigDecimal fee = DECIMAL_CONVERTER.fromBytes(resultMessage.getFee(), null, decimaltype);

        assertEquals(transactionEvent.getTransactionDto().getAmount(), amount);
        assertEquals(transactionEvent.getTransactionDto().getFee(), fee);
        assertEquals(transactionEvent.getTransactionDto().getDate(), Instant.ofEpochMilli(resultMessage.getDate()));
        assertEquals(transactionEvent.getTransactionDto().getIban(), resultMessage.getIban());
        assertEquals(transactionEvent.getTransactionDto().getDescription(), resultMessage.getDescription());
        assertEquals(transactionEvent.getTransactionDto().getReference(), resultMessage.getReference());
    }
}
