package com.wayqui.transaq.conf.kafka.streams.serdes;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.math.BigDecimal;

public class BigDecimalSerde implements Serde<BigDecimal> {

    @Override
    public Serializer<BigDecimal> serializer() {
        return new BigDecimalSerializer();
    }

    @Override
    public Deserializer<BigDecimal> deserializer() {
        return new BigDecimalDeserializer();
    }
}
