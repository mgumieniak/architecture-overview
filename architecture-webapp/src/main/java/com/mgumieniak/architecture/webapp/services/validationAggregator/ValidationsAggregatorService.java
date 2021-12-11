package com.mgumieniak.architecture.webapp.services.validationAggregator;

import com.mgumieniak.architecture.models.Order;
import com.mgumieniak.architecture.models.OrderState;
import com.mgumieniak.architecture.models.OrderValidation;
import com.mgumieniak.architecture.models.OrderValidationResults;
import com.mgumieniak.architecture.webapp.kafka.Topic;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.StreamJoined;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.KafkaStreamBrancher;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class ValidationsAggregatorService {

    private final Topic<String, OrderValidation> orderValidationTopic;
    private final Topic<String, Order> orderTopic;
    private final Serde<OrderValidationResults> validationResultsSerde;

    @Autowired
    public void check(final @NonNull StreamsBuilder builder) {
        val orderIdToOrderValidation = builder
                .stream(orderValidationTopic.getName(), orderValidationTopic.getConsumed());

        val orderIdToOrder = builder
                .stream(orderTopic.getName(), orderTopic.getConsumed())
                .filter((orderId, order) -> order.getState() == OrderState.CREATED);

        val orderIdToValidationResults = orderIdToOrderValidation
                .groupByKey()
                .windowedBy(TimeWindows
                        .ofSizeAndGrace(Duration.ofMinutes(1), Duration.ofSeconds(5))
                )
                .aggregate(
                        () -> OrderValidationResults.builder().build(),
                        (orderId, orderValidation, validationResults) ->
                                OrderValidationResults.changeValidationResult(validationResults, orderValidation),
                        Materialized.with(null, validationResultsSerde)
                )
                .toStream((windowedOrderId, validationResults) -> windowedOrderId.key())
                .peek((key, value) -> log.info("[orderIdToValidationResults] key: {} value: {}", key, value))
                .filter((k, v) -> v != null && v.isDefined());


        new KafkaStreamBrancher<String, OrderValidationResults>()
                .branch(
                        (orderId, validationResults) -> validationResults.isPassed(),
                        kStream -> kStream
                                .join(orderIdToOrder,
                                        (validationResults, order) -> Order.changeState(order, OrderState.VALIDATED),
                                        JoinWindows.of(Duration.ofDays(100)),
                                        StreamJoined.with(Serdes.String(), validationResultsSerde, orderTopic.getVSerde())
                                )
                                .peek((key, value) -> log.info("[Order passed validation req] key: {} value: {}", key, value))
                                .to(orderTopic.getName(), orderTopic.getProduced())
                )
                .branch(
                        (orderId, validationResults) -> validationResults.isNotPassed(),
                        kStream -> kStream
                                .join(orderIdToOrder,
                                        (validationResults, order) -> Order.changeState(order, OrderState.FAILED),
                                        JoinWindows.of(Duration.ofMinutes(10)),
                                        StreamJoined.with(Serdes.String(), validationResultsSerde, orderTopic.getVSerde())
                                )
                                .peek((key, value) -> log.info("[One of the order validation failed] key: {} value: {}", key, value))
                                .to(orderTopic.getName(), orderTopic.getProduced())
                )
                .onTopOf(orderIdToValidationResults);

    }
}
