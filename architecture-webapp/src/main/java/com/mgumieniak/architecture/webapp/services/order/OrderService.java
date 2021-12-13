package com.mgumieniak.architecture.webapp.services.order;

import com.mgumieniak.architecture.models.orders.Order;
import com.mgumieniak.architecture.models.orders.OrderDTO;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.mgumieniak.architecture.models.orders.OrderDTO.fromOrderDTO;
import static com.mgumieniak.architecture.webapp.kafka.topics.Topics.ORDERS;

@Service
@RequiredArgsConstructor
public class OrderService {

    @Qualifier("txKafkaTemplate")
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public void createOrder(@NonNull final OrderDTO orderDTO) {
        final Order order = fromOrderDTO(orderDTO);
        kafkaTemplate.send(ORDERS, String.valueOf(order.getId()), order);
    }

}
