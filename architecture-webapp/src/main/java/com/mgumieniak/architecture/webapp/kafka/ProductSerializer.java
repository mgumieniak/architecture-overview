package com.mgumieniak.architecture.webapp.kafka;

import com.mgumieniak.architecture.models.products.Product;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.charset.StandardCharsets;

public class ProductSerializer implements Serializer<Product> {

    @Override
    public byte[] serialize(String topic, Product pt) {
        return pt.toString().getBytes(StandardCharsets.UTF_8);
    }
}
