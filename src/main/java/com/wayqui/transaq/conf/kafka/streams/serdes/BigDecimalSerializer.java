package com.wayqui.transaq.conf.kafka.streams.serdes;

import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;

import java.math.BigDecimal;

public class BigDecimalSerializer implements Serializer<BigDecimal> {

    @Override
    public byte[] serialize(String s, BigDecimal bigDecimal) {
        return bigDecimal.toPlainString().getBytes();
    }

    @Override
    public byte[] serialize(String topic, Headers headers, BigDecimal data) {
        return data.toPlainString().getBytes();
    }
}
