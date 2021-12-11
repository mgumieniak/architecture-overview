package com.mgumieniak.architecture.webapp.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mgumieniak.architecture.models.Order;
import com.mgumieniak.architecture.models.OrderValidation;
import com.mgumieniak.architecture.models.OrderValidationResults;
import com.mgumieniak.architecture.models.OrderValue;
import com.mgumieniak.architecture.models.products.Product;
import lombok.AllArgsConstructor;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;

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
    public Serde<OrderValidationResults> buildOrderValidationResultsSerde() {
        return new JsonSerde<>(OrderValidationResults.class, mapper);
    }

    @Bean
    public Serde<Product> buildProductSerde() {
        return new ProductTypeSerde();
    }
}
