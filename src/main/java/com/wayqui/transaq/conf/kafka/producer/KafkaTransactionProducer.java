package com.wayqui.transaq.conf.kafka.producer;

import com.wayqui.avro.TransactionAvro;
import com.wayqui.transaq.conf.kafka.model.TransactionEvent;
import com.wayqui.transaq.dto.TransactionDto;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

public interface KafkaTransactionProducer {

    /**
     * This method sends a message asynchronously to a topic passed as parameter
     * @param event the transaction event to send
     * @param topic the topic that will store the message
     * @return
     */
    ListenableFuture<SendResult<Long, TransactionAvro>> sendAsync(TransactionEvent event, String topic);

    /**
     * This method sends a message asynchronously to the default topic (configured
     * in application.yml file in default-topic
     * @param key the key to the message
     * @param transactionDto the transaction dto to send
     */
    void sendAsyncDefaultTopic(Long key, TransactionDto transactionDto);

}
