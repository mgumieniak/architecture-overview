package com.mgumieniak.architecture.webapp.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mgumieniak.architecture.models.Order;
import com.mgumieniak.architecture.models.OrderValidation;
import com.mgumieniak.architecture.models.OrderValue;
import com.mgumieniak.architecture.models.Product;
import lombok.AllArgsConstructor;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.kafka.support.serializer.ToFromStringSerde;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Configuration
@AllArgsConstructor
public class JsonSerdes {

    private final ObjectMapper mapper;

    @Bean
    public Serde<Order> buildOrderSerde() {
        return new JsonSerde<>(Order.class, mapper);
    }

    @Bean
    public Serde<OrderValue> buildOrderValueSerde() {
        return new JsonSerde<>(OrderValue.class, mapper);
    }

    @Bean
    public Serde<OrderValidation> buildOrderValidationSerde() {
        return new JsonSerde<>(OrderValidation.class, mapper);
    }

    @Bean
    public Serde<Product> buildProductSerde() {
        return new ProductTypeSerde();
    }

    private static final class ProductTypeSerde implements Serde<Product> {

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
}
