package com.mgumieniak.architecture.webapp.controllers.orders;

import com.mgumieniak.architecture.models.orders.OrderDTO;
import com.mgumieniak.architecture.webapp.services.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static com.mgumieniak.architecture.connectors.Paths.INTERNAL;
import static com.mgumieniak.architecture.connectors.Paths.Order.ORDERS;

@RestController
@RequestMapping(INTERNAL + ORDERS)
@RequiredArgsConstructor
public class InternalOrderController{

    private final OrderService orderService;

    public Mono<OrderDTO> getLocallyOrder(String orderId) {
        return orderService.getLocallyOrder(orderId);
    }

}
