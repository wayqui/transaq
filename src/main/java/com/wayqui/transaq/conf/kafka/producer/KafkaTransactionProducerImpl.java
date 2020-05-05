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
    KafkaTemplate<Integer, String> kafkaTemplate;

    @Override
    public void sendAsyncDefaultTopic(TransactionEvent event) {
        String transactJSON = new Gson().toJson(event.getTransactionDto());

        ListenableFuture<SendResult<Integer, String>> listenerFuture =
                kafkaTemplate.sendDefault(event.getId(), transactJSON);
        listenerFuture.addCallback(new ListenerCallback(event.getId(), transactJSON));
    }

    @Override
    public void sendAsync(TransactionEvent event, String topic) {
        String transactJSON = new Gson().toJson(event.getTransactionDto());

        ProducerRecord<Integer, String> producerRecord =
                new ProducerRecord<>(topic, null, event.getId(), transactJSON, event.getRecordHeaders());

        ListenableFuture<SendResult<Integer, String>> listenerFuture =
                kafkaTemplate.send(producerRecord);
        listenerFuture.addCallback(new ListenerCallback(event.getId(), transactJSON));
    }

    private static class ListenerCallback implements ListenableFutureCallback<SendResult<Integer, String>> {

        private Integer key;
        private String value;

        public ListenerCallback(Integer key, String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public void onFailure(Throwable throwable) {
            log.error("Error message {}", throwable.getMessage());
        }

        @Override
        public void onSuccess(SendResult<Integer, String> integerStringSendResult) {
            log.info("This is the message {} ==> {} and the partition is {}",key, value, integerStringSendResult.getRecordMetadata().partition());
        }
    }
}
