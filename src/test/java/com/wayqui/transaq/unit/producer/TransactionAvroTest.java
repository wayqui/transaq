package com.wayqui.transaq.unit.producer;

import com.wayqui.avro.TransactionAvro;
import org.apache.avro.Conversions;
import org.apache.avro.LogicalTypes;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.UUID;

public class TransactionAvroTest {

    @Test
    public void testingSpecificAvroRecord() {
        TransactionAvro.Builder builder = TransactionAvro.newBuilder();

        BigDecimal testing = new BigDecimal("15.4");

        ByteBuffer byteBuff = ByteBuffer.wrap(testing.unscaledValue().toByteArray());
        builder.setAmount(byteBuff);
        builder.setFee(byteBuff);
        builder.setDate(Instant.now().toEpochMilli());
        builder.setDescription("This is a transaction");
        builder.setIban("IBAN");
        builder.setReference(UUID.randomUUID().toString());

        System.out.println(builder.build());
                
    }

    @Test
    public void testingDecimalConversion() {
        Conversions.DecimalConversion DECIMAL_CONVERTER = new Conversions.DecimalConversion();

        BigDecimal d = BigDecimal.valueOf(3.1415);

        LogicalTypes.Decimal decimaltype = LogicalTypes.decimal(7, 4);

        ByteBuffer buffer = DECIMAL_CONVERTER.toBytes(d, null, decimaltype);

        System.out.println(DECIMAL_CONVERTER.fromBytes(buffer, null, decimaltype).toString());

        BigDecimal n = DECIMAL_CONVERTER.fromBytes(buffer, null, decimaltype);

        System.out.println(n.toString());
    }
}
