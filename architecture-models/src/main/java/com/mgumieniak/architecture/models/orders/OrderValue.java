package com.mgumieniak.architecture.models.orders;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class OrderValue {
    Order order;
    double value;

    public static OrderValue buildDef() {
        return OrderValue.builder()
                .order(Order.buildDef())
                .value(0.0)
                .build();
    }
}
