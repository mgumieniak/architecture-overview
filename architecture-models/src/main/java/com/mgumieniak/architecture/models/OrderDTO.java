package com.mgumieniak.architecture.models;


import com.mgumieniak.architecture.models.products.Product;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Value
@Builder
public class OrderDTO implements Timestamp{

    @NotNull
    String id;

    @Min(value = 0)
    long customerId;

    @NotNull
    OrderState state;

    @NotNull
    Product product;

    @Min(value = 0)
    int quantity;

    @Min(value = 0)
    double price;

    @NotNull
    Instant timestamp;

    public static OrderDTO toOrderDTO(final Order order) {
        return new OrderDTO(order.getId(),
                order.getCustomerId(),
                order.getState(),
                order.getProduct(),
                order.getQuantity(),
                order.getPrice(),
                order.getTimestamp());
    }

    public static Order fromOrderDTO(final OrderDTO order) {
        return new Order(order.getId(),
                order.getCustomerId(),
                order.getState(),
                order.getProduct(),
                order.getQuantity(),
                order.getPrice(),
                order.getTimestamp());
    }

}