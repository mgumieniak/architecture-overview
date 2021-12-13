package com.mgumieniak.architecture.webapp.controllers;

import com.mgumieniak.architecture.models.orders.OrderDTO;
import com.mgumieniak.architecture.webapp.services.order.OrderService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrdersController {


    private final OrderService orderService;

    @PostMapping
    public void createOrder(final @NonNull @RequestBody @Valid OrderDTO order) {
        orderService.createOrder(order);
    }

}
