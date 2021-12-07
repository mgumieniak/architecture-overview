package com.mgumieniak.architecture.webapp.services.order.details;

import com.mgumieniak.architecture.models.Order;
import com.mgumieniak.architecture.models.OrderValidation;
import com.mgumieniak.architecture.webapp.kafka.Topic;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.mgumieniak.architecture.webapp.kafka.Topics.ORDER_VALIDATIONS;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderDetailsService {

    private final Serde<OrderValidation> validationSerde;
    private final OrderDetailsCheckService detailsCheckService;
    private final Topic<String, Order> orderTopic;

    @Autowired
    public void check(final @NonNull StreamsBuilder builder) {
        builder
                .stream(orderTopic.getName(), orderTopic.getConsumed())
                .mapValues(detailsCheckService::createOrderValidation)
                .to(ORDER_VALIDATIONS, Produced.with(Serdes.String(), validationSerde));
    }
}
