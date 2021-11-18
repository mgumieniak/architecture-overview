package com.mgumieniak.architecture.webapp.services.order;

import com.mgumieniak.architecture.models.Order;
import com.mgumieniak.architecture.models.OrderDTO;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.mgumieniak.architecture.models.OrderDTO.fromOrderDTO;
import static com.mgumieniak.architecture.webapp.configs.Topics.ORDERS;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public void submitOrder(@NonNull final OrderDTO orderDTO) {
        final Order order = fromOrderDTO(orderDTO);
        kafkaTemplate.send(ORDERS, String.valueOf(order.getCustomerId()), order);
    }
}