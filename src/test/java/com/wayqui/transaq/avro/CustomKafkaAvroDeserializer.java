package com.wayqui.transaq.avro;

import com.wayqui.avro.TransactionAvro;
import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import org.apache.avro.Schema;

public class CustomKafkaAvroDeserializer extends KafkaAvroDeserializer {

    @Override
    public Object deserialize(String s, byte[] bytes) {
        if (s.equals("transaction-events")) {
            this.schemaRegistry = getMockClient(TransactionAvro.SCHEMA$);
        }
        return super.deserialize(bytes, TransactionAvro.SCHEMA$);
    }

    private static SchemaRegistryClient getMockClient(final Schema schema$) {
        return new MockSchemaRegistryClient() {
            @Override
            public synchronized Schema getById(int id) {
                return schema$;
            }
        };
    }
}
