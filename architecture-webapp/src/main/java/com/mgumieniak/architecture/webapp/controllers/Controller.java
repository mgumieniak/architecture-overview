package com.mgumieniak.architecture.webapp.controllers;

import com.mgumieniak.architecture.models.OrderDTO;
import com.mgumieniak.architecture.webapp.services.order.OrderService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class Controller {


    private final OrderService orderService;

    @PostMapping
    public void submitOrder(final @NonNull @RequestBody @Valid OrderDTO order) {
        orderService.submitOrder(order);
    }

//    @GetMapping()
//    public void submitOrder(final @NonNull @RequestBody @Valid OrderBean order) {
//        orderService.submitOrder(order);
//    }

}
