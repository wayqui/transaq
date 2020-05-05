package com.wayqui.transaq.conf.kafka.producer;

import com.wayqui.transaq.conf.kafka.model.TransactionEvent;

public interface KafkaTransactionProducer {

    /**
     * This method sends a message asynchronously to a topic passed as parameter
     * @param event the transaction event to send
     * @param topic the topic that will store the message
     */
    void sendAsync(TransactionEvent event, String topic);

    /**
     * This method sends a message asynchronously to the default topic (configured
     * in application.yml file in default-topic
     * @param event the transaction event to send
     */
    void sendAsyncDefaultTopic(TransactionEvent event);

}
