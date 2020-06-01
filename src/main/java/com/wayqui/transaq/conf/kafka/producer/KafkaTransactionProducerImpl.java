package com.wayqui.transaq.conf.kafka.producer;

import com.wayqui.avro.TransactionAvro;
import com.wayqui.transaq.conf.kafka.model.TransactionEvent;
import com.wayqui.transaq.dto.TransactionDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Conversions;
import org.apache.avro.LogicalTypes;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.nio.ByteBuffer;

@Component
@Slf4j
public class KafkaTransactionProducerImpl implements KafkaTransactionProducer {

    @Autowired
    KafkaTemplate<Long, TransactionAvro> kafkaTemplate;

    @Override
    public void sendAsyncDefaultTopic(TransactionEvent event) {
        TransactionDto dto = event.getTransactionDto();

        Conversions.DecimalConversion DECIMAL_CONVERTER = new Conversions.DecimalConversion();

        LogicalTypes.Decimal decimaltype = LogicalTypes.decimal(6, 2);

        ByteBuffer feeBuff = DECIMAL_CONVERTER.toBytes(dto.getFee(), null, decimaltype);
        ByteBuffer amountBuff = DECIMAL_CONVERTER.toBytes(dto.getAmount(), null, decimaltype);

        TransactionAvro transactMsg = TransactionAvro.newBuilder()
                .setFee(feeBuff)
                .setAmount(amountBuff)
                .setReference(dto.getReference())
                .setIban(dto.getIban())
                .setDescription(dto.getDescription())
                .setDate(dto.getDate().toInstant().toEpochMilli())
                .build();

        ListenableFuture<SendResult<Long, TransactionAvro>> listenerFuture =
                kafkaTemplate.sendDefault(event.getId(), transactMsg);
        listenerFuture.addCallback(new ListenerCallback(event.getId(), transactMsg));
    }

    @Override
    public ListenableFuture<SendResult<Long, TransactionAvro>> sendAsync(TransactionEvent event, String topic) {
        TransactionDto dto = event.getTransactionDto();

        ByteBuffer feeBuff = ByteBuffer.wrap(dto.getFee().unscaledValue().toByteArray());
        ByteBuffer amountBuff = ByteBuffer.wrap(dto.getAmount().unscaledValue().toByteArray());

        TransactionAvro transactMsg = TransactionAvro.newBuilder()
                .setFee(feeBuff)
                .setAmount(amountBuff)
                .setReference(dto.getReference())
                .setIban(dto.getIban())
                .setDescription(dto.getDescription())
                .setDate(dto.getDate().toInstant().toEpochMilli())
                .build();

        ProducerRecord<Long, TransactionAvro> producerRecord =
                new ProducerRecord<>(topic, null, null, event.getId(), transactMsg, event.getRecordHeaders());

        ListenableFuture<SendResult<Long, TransactionAvro>> listenerFuture =
                kafkaTemplate.send(producerRecord);
        listenerFuture.addCallback(new ListenerCallback(event.getId(), transactMsg));

        return listenerFuture;
    }

    private static class ListenerCallback implements ListenableFutureCallback<SendResult<Long, TransactionAvro>> {

        private Long key;
        private TransactionAvro value;

        public ListenerCallback(Long key, TransactionAvro value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public void onFailure(Throwable throwable) {
            log.error("Error sending message {}", throwable.getMessage());
            try {
                throw throwable;
            } catch (Throwable e) {
                log.error("Error in onFailure {}", e.getMessage());
            }
        }

        @Override
        public void onSuccess(SendResult<Long, TransactionAvro> integerStringSendResult) {
            log.info("This is the message {} ==> {} and the partition is {}",key, value, integerStringSendResult.getRecordMetadata().partition());
        }
    }
}
