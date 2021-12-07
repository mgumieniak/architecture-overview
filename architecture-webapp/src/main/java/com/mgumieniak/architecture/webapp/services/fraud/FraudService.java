package com.mgumieniak.architecture.webapp.services.fraud;

import com.mgumieniak.architecture.models.Order;
import com.mgumieniak.architecture.models.OrderState;
import com.mgumieniak.architecture.models.OrderValidation;
import com.mgumieniak.architecture.models.OrderValue;
import com.mgumieniak.architecture.webapp.kafka.Topic;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.SessionStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.KafkaStreamBrancher;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static com.mgumieniak.architecture.models.OrderValidationResult.FAIL;
import static com.mgumieniak.architecture.models.OrderValidationResult.PASS;
import static com.mgumieniak.architecture.models.OrderValidationType.FRAUD_CHECK;
import static com.mgumieniak.architecture.webapp.kafka.Stores.DS_LATEST_ORDER_WITH_MONEY_SPENT;

@Slf4j
@Service
@RequiredArgsConstructor
public class FraudService {

    private static final Duration INACTIVITY_GAP = Duration.ofHours(1);
    private static final Duration GRACE_PERIOD = Duration.ofMinutes(1);

    private final FraudDetectionService fraudDetectionService;
    private final Serde<Order> orderSerde;
    private final Serde<OrderValue> orderValueSerde;

    private final Topic<String, Order> orderTopic;
    private final Topic<String, OrderValidation> orderValidationTopic;

    @Autowired
    public void check(final @NonNull StreamsBuilder streamsBuilder) {
        val customerIdToCreatedOrder = getCreatedOrdersStream(streamsBuilder);
        log(customerIdToCreatedOrder);
        val windowedCustomerIdToOrderValue = createLatestOrderWithAggregatedSpent(customerIdToCreatedOrder);
        checkIsOrderFraudulent(windowedCustomerIdToOrderValue);
    }

    private void log(final KStream<String, Order> customerIdToCreatedOrderKS) {
        customerIdToCreatedOrderKS
                .peek((key, value) -> log.info("[FraudService] Created order key:{} value:{}", key, value.toString()));
    }

    private KStream<String, Order> getCreatedOrdersStream(final @NonNull StreamsBuilder streamsBuilder) {
        return streamsBuilder
                .stream(orderTopic.getName(), orderTopic.getConsumed())
                .filter(((orderId, order) -> OrderState.CREATED.equals(order.getState())));
    }

    private KTable<Windowed<String>, OrderValue> createLatestOrderWithAggregatedSpent(final @NonNull KStream<String, Order> customerIdToCreatedOrderKS) {
        return customerIdToCreatedOrderKS
                .groupByKey(Grouped.with(Serdes.String(), orderSerde))
                .windowedBy(SessionWindows
                        .ofInactivityGapAndGrace(INACTIVITY_GAP, GRACE_PERIOD))
                .aggregate(OrderValue::buildDef,
                        (customerId, order, orderValue) -> OrderValue.builder()
                                .order(order)
                                .value(orderValue.getValue() + order.getQuantity() * order.getPrice())
                                .build(),
                        (key, orderValue, anotherOrderValue) ->
                                new OrderValue(orderValue.getOrder(), orderValue.getValue() + anotherOrderValue.getValue()),
                        Materialized.<String, OrderValue, SessionStore<Bytes, byte[]>>
                                as(DS_LATEST_ORDER_WITH_MONEY_SPENT)
                                .withKeySerde(Serdes.String())
                                .withValueSerde(orderValueSerde)
                );
    }

    private void checkIsOrderFraudulent(final @NonNull KTable<Windowed<String>, OrderValue> windowedCustomerIdToOrderValue) {
        final KStream<String, OrderValue> customerIdToOrderValue = windowedCustomerIdToOrderValue
                .toStream((windowKey, orderValues) -> windowKey.key())
                .filter((key, value) -> value != null)
                .selectKey((id, orderValue) -> orderValue.getOrder().getId())
                .peek(((key, value) -> log.info("Key: {}; Value: {}", key, value)));

        new KafkaStreamBrancher<String, OrderValue>()
                .branch(
                        (orderId, orderValues) -> fraudDetectionService.isMoneyLimitExceeded(orderValues),
                        kStream -> kStream.mapValues(orderValue ->
                                new OrderValidation(orderValue.getOrder().getId(), FRAUD_CHECK, FAIL))
                                .to(orderValidationTopic.getName(), orderValidationTopic.getProduced()))
                .branch((orderId, orderValues) -> fraudDetectionService.isMoneyBelowLimit(orderValues),
                        kStream -> kStream.mapValues(orderValue ->
                                new OrderValidation(orderValue.getOrder().getId(), FRAUD_CHECK, PASS))
                                .to(orderValidationTopic.getName(), orderValidationTopic.getProduced()))
                .onTopOf(customerIdToOrderValue);
    }
}
