package com.mgumieniak.architecture.webapp.services.order.details;

import com.mgumieniak.architecture.models.orders.Order;
import com.mgumieniak.architecture.models.orders.OrderState;
import com.mgumieniak.architecture.models.validations.OrderValidation;
import com.mgumieniak.architecture.webapp.kafka.topics.Topic;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.StreamsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderDetailsService {

    private final OrderDetailsCheckService detailsCheckService;
    private final Topic<String, Order> orderTopic;
    private final Topic<String, OrderValidation> orderValidationTopic;

    @Autowired
    public void check(final @NonNull StreamsBuilder builder) {
        builder
                .stream(orderTopic.getName(), orderTopic.getConsumed())
                .filter((orderId, order) -> order.getState() == OrderState.CREATED)
                .mapValues(detailsCheckService::createOrderValidation)
                .to(orderValidationTopic.getName(), orderValidationTopic.getProduced());
    }
}
