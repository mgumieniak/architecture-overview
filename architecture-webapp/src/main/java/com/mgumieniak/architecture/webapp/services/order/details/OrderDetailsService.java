package com.mgumieniak.architecture.webapp.services.order.details;

import com.mgumieniak.architecture.models.Order;
import com.mgumieniak.architecture.models.OrderValidation;
import com.mgumieniak.architecture.webapp.kafka.OrderTimestampExtractor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.mgumieniak.architecture.webapp.kafka.Topics.ORDERS;
import static com.mgumieniak.architecture.webapp.kafka.Topics.ORDER_VALIDATIONS;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderDetailsService {

    private final Serde<Order> orderSerde;
    private final Serde<OrderValidation> validationSerde;
    private final OrderTimestampExtractor orderTimestampExtractor;
    private final OrderDetailsCheckService detailsCheckService;

    @Autowired
    public void check(final @NonNull StreamsBuilder builder) {
        builder
                .stream(ORDERS, consumeOrders())
                .mapValues(detailsCheckService::createOrderValidation)
                .to(ORDER_VALIDATIONS, Produced.with(Serdes.String(), validationSerde));
    }

    private Consumed<String, Order> consumeOrders() {
        return Consumed.with(Serdes.String(), orderSerde)
                .withTimestampExtractor(orderTimestampExtractor);
    }
}
