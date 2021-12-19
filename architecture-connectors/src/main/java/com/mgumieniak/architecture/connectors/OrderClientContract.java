package com.mgumieniak.architecture.connectors;

import com.mgumieniak.architecture.models.orders.OrderDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

import java.net.URI;

import static com.mgumieniak.architecture.connectors.Paths.Order.ORDERS;

public interface OrderClientContract {

    @GetMapping("/{id}")
    Mono<OrderDTO> getOrder(URI baseUrl,
                            @PathVariable("id") String orderId);
}
