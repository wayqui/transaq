package com.wayqui.transaq.conf.kafka.producer;

import com.google.gson.Gson;
import com.wayqui.transaq.conf.kafka.model.TransactionEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Component
@Slf4j
public class KafkaTransactionProducerImpl implements KafkaTransactionProducer {

    @Autowired
    KafkaTemplate<Long, String> kafkaTemplate;

    @Override
    public void sendAsyncDefaultTopic(TransactionEvent event) {
        String transactJSON = new Gson().toJson(event.getTransactionDto());

        ListenableFuture<SendResult<Long, String>> listenerFuture =
                kafkaTemplate.sendDefault(event.getId(), transactJSON);
        listenerFuture.addCallback(new ListenerCallback(event.getId(), transactJSON));
    }

    @Override
    public ListenableFuture<SendResult<Long, String>> sendAsync(TransactionEvent event, String topic) {
        String transactJSON = new Gson().toJson(event.getTransactionDto());

        ProducerRecord<Long, String> producerRecord =
                new ProducerRecord<>(topic, null, null, event.getId(), transactJSON, event.getRecordHeaders());

        ListenableFuture<SendResult<Long, String>> listenerFuture =
                kafkaTemplate.send(producerRecord);
        listenerFuture.addCallback(new ListenerCallback(event.getId(), transactJSON));

        return listenerFuture;
    }

    private static class ListenerCallback implements ListenableFutureCallback<SendResult<Long, String>> {

        private Long key;
        private String value;

        public ListenerCallback(Long key, String value) {
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
        public void onSuccess(SendResult<Long, String> integerStringSendResult) {
            log.info("This is the message {} ==> {} and the partition is {}",key, value, integerStringSendResult.getRecordMetadata().partition());
        }
    }
}
