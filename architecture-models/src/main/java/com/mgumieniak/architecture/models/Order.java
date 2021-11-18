package com.mgumieniak.architecture.models;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class Order implements Timestamp{
    String id;
    long customerId;
    OrderState state;
    Product product;
    int quantity;
    double price;
    Instant timestamp;

    public static Order buildDef(){
        return Order.builder()
                .customerId(0L)
                .quantity(0)
                .price(0.0)
                .build();
    }
}
