package com.wayqui.transaq.conf.kafka;

import com.wayqui.avro.TransactionAvro;
import com.wayqui.transaq.conf.kafka.streams.serdes.BigDecimalSerde;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Conversions;
import org.apache.avro.LogicalTypes;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;

import java.math.BigDecimal;
import java.util.Collections;

@Configuration
@EnableKafka
@EnableKafkaStreams
@Slf4j
public class KafkaStreamsConfig {

    @Value(value = "${spring.kafka.streams.properties.schema.registry.url}")
    private String registryAddress;

    @Autowired
    private Serde<TransactionAvro> transactionAvroSerde;

    @Bean
    @Scope("singleton")
    public Serde<TransactionAvro> transactionAvroSerde() {
        final Serde<TransactionAvro> specificAvroSerde = new SpecificAvroSerde<>();
        final boolean isKeySerde = false;
        specificAvroSerde.configure(
                Collections.singletonMap("schema.registry.url", registryAddress),
                isKeySerde);
        return specificAvroSerde;
    }

    @Bean
    public KStream<Long, TransactionAvro> kStream(StreamsBuilder kStreamBuilder) {
        log.info("Processing kstream...");

        Conversions.DecimalConversion DECIMAL_CONVERTER = new Conversions.DecimalConversion();

        KStream<Long, TransactionAvro> stream = kStreamBuilder
                .stream("transaction-events", Consumed.with(Serdes.Long(), transactionAvroSerde));

        stream
                .peek((k, v) -> log.info("Before... {}=>{}", k, v))
                .map((k, v) -> {
                    BigDecimal fee = DECIMAL_CONVERTER.fromBytes(v.getFee(), null, LogicalTypes.decimal(6, 2));
                    BigDecimal amount = DECIMAL_CONVERTER.fromBytes(v.getAmount(), null, LogicalTypes.decimal(6, 2));

                    return new KeyValue<>(v.getIban(), amount.subtract(fee));
                })
                .groupByKey()
                .aggregate(
                        () -> new BigDecimal("0"), /* initializer */
                        (aggKey, newValue, aggValue) -> aggValue.add(newValue), /* adder */
                        Materialized.as("materialized") /* state store name */
                )
                .toStream()
                .peek((k, v) -> log.info("After... {}=>{}", k, v))
                .to("account-balance", Produced.with(Serdes.String() , new BigDecimalSerde()));

        return stream;
    }

}