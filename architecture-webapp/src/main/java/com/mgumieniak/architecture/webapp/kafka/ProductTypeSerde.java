package com.mgumieniak.architecture.webapp.kafka;

import com.mgumieniak.architecture.models.products.Product;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.charset.StandardCharsets;
import java.util.Map;


public class ProductTypeSerde implements Serde<Product> {


    @Override
    public void configure(final Map<String, ?> map, final boolean b) {
    }

    @Override
    public void close() {
    }

    @Override
    public Serializer<Product> serializer() {
        return new Serializer<>() {
            @Override
            public void configure(final Map<String, ?> map, final boolean b) {
            }

            @Override
            public byte[] serialize(final String topic, final Product pt) {
                return pt.toString().getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public void close() {
            }
        };
    }

    @Override
    public Deserializer<Product> deserializer() {
        return new Deserializer<>() {
            @Override
            public void configure(final Map<String, ?> map, final boolean b) {
            }

            @Override
            public Product deserialize(final String topic, final byte[] bytes) {
                return Product.valueOf(new String(bytes, StandardCharsets.UTF_8));
            }

            @Override
            public void close() {
            }
        };
    }
}
