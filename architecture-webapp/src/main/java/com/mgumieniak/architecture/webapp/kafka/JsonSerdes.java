package com.mgumieniak.architecture.webapp.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mgumieniak.architecture.models.Order;
import com.mgumieniak.architecture.models.OrderValidation;
import com.mgumieniak.architecture.models.OrderValue;
import lombok.AllArgsConstructor;
import org.apache.kafka.common.serialization.Serde;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;

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
}
