package com.mgumieniak.architecture.webapp;

import com.mgumieniak.architecture.models.*;
import com.mgumieniak.architecture.models.products.Product;

import java.time.Instant;

public class ObjectCreator {

    public static Order createOrder(String orderId, Long customerId) {
        return getOrderBuilder(orderId, Instant.now())
                .customerId(customerId)
                .build();
    }

    public static Order createOrder(String orderId) {
        return getOrderBuilder(orderId, Instant.now())
                .build();
    }

    public static Order createOrder(String orderId, Instant timestamp) {
        return getOrderBuilder(orderId, timestamp)
                .build();
    }

    private static Order.OrderBuilder getOrderBuilder(String orderId, Instant timestamp) {
        return Order.builder()
                .id(orderId)
                .customerId(2)
                .state(OrderState.CREATED)
                .product(Product.UNDERPANTS)
                .quantity(1)
                .price(12.00)
                .timestamp(timestamp);
    }

    public static OrderValidation createPassedValidation(String orderId, OrderValidationType fraudCheck) {
        return createValidation(orderId, fraudCheck, OrderValidationResult.PASS);
    }

    public static OrderValidation createValidation(String orderId, OrderValidationType fraudCheck,
                                             OrderValidationResult validationResult) {
        return OrderValidation.builder()
                .orderId(orderId)
                .checkType(fraudCheck)
                .validationResult(validationResult)
                .build();
    }
}
