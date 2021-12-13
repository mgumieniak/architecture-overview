package com.mgumieniak.architecture.webapp.kafka.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mgumieniak.architecture.models.*;
import com.mgumieniak.architecture.models.orders.Order;
import com.mgumieniak.architecture.models.orders.OrderValue;
import com.mgumieniak.architecture.models.products.Product;
import com.mgumieniak.architecture.models.validations.OrderValidation;
import com.mgumieniak.architecture.models.validations.OrderValidationResults;
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

    @Bean
    public Serde<OrderValidationResults> buildOrderValidationResultsSerde() {
        return new JsonSerde<>(OrderValidationResults.class, mapper);
    }

    @Bean
    public Serde<Product> buildProductSerde() {
        return new ProductTypeSerde();
    }

    @Bean
    public Serde<Payment> buildPaymentSerde() {
        return new JsonSerde<>(Payment.class, mapper);
    }

    @Bean
    public Serde<Email> buildEmailSerde() {
        return new JsonSerde<>(Email.class, mapper);
    }

    @Bean
    public Serde<Customer> buildCustomerSerde() {
        return new JsonSerde<>(Customer.class, mapper);
    }
}
