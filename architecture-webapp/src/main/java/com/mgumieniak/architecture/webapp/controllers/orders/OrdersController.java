package com.mgumieniak.architecture.webapp.controllers.orders;

import com.mgumieniak.architecture.models.orders.OrderDTO;
import com.mgumieniak.architecture.webapp.services.order.OrderService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

import static com.mgumieniak.architecture.connectors.Paths.Order.ORDERS;

@RestController
@RequestMapping(ORDERS)
@RequiredArgsConstructor
public class OrdersController {

    private final OrderService orderService;

    @PostMapping
    public void createOrder(final @NonNull @RequestBody @Valid OrderDTO order) {
        orderService.createOrder(order);
    }

    @GetMapping("/{id}")
    public Mono<OrderDTO> getOrder(@PathVariable("id") String orderId) {
        return orderService.getOrder(orderId);
    }
}
