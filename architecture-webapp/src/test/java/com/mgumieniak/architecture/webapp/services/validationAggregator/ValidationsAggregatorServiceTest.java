package com.mgumieniak.architecture.webapp.services.validationAggregator;

import com.mgumieniak.architecture.models.*;
import com.mgumieniak.architecture.webapp.BasicStreamTest;
import com.mgumieniak.architecture.webapp.kafka.topics.Topic;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.test.TestRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Instant;
import java.util.List;

import static com.mgumieniak.architecture.webapp.ObjectCreator.*;
import static org.assertj.core.api.Assertions.assertThat;

class ValidationsAggregatorServiceTest extends BasicStreamTest {

    private TestInputTopic<String, Order> orderInputTopic;
    private TestInputTopic<String, OrderValidation> orderValidationInputTopic;
    private TestOutputTopic<String, Order> orderOutputTopic;

    private final Topic<String, OrderValidation> orderValidationTopic = topics.createOrderValidationTopic();
    private final Topic<String, Order> orderTopic = topics.createOrderTopic();
    private final Serde<OrderValidationResults> validationResultsSerde = jsonSerdes.buildOrderValidationResultsSerde();

    final ValidationsAggregatorService validationsAggregatorService = new ValidationsAggregatorService(
            orderValidationTopic,
            orderTopic,
            validationResultsSerde
    );

    @BeforeEach
    void setUp() {
        final StreamsBuilder builder = new StreamsBuilder();
        validationsAggregatorService.check(builder);

        testDriver = new TopologyTestDriver(builder.build(), streamProperties);

        orderInputTopic = createInputTopic(orderTopic);
        orderValidationInputTopic = createInputTopic(orderValidationTopic);
        orderOutputTopic = createOutputTopic(orderTopic);
    }

    @AfterEach
    void tearDown() {
        testDriver.close();
    }

    @Test
    void shouldValidateOrderWhenAllValidationPassed() {
        String orderId = "1";
        orderInputTopic.pipeInput(orderId, createOrder(orderId, Instant.now()));
        orderValidationInputTopic.pipeRecordList(
                List.of(
                        new TestRecord<>(orderId, createPassedValidation(orderId, OrderValidationType.FRAUD_CHECK)),
                        new TestRecord<>(orderId, createPassedValidation(orderId, OrderValidationType.INVENTORY_CHECK)),
                        new TestRecord<>(orderId, createPassedValidation(orderId, OrderValidationType.ORDER_DETAILS_CHECK))
                )
        );

        Order acceptedOrder = orderOutputTopic.readKeyValue().value;
        assertThat(acceptedOrder.getId())
                .isEqualTo(orderId);
        assertThat(acceptedOrder.getState())
                .isEqualTo(OrderState.VALIDATED);
    }

    @ParameterizedTest
    @CsvSource({
            "PASS, PASS, FAIL",
            "PASS, FAIL, PASS",
            "FAIL, PASS, PASS"
    })
    void shouldNotValidateOrderWhenOneOfValidationFailed(OrderValidationResult fraudValidationResult,
                                                         OrderValidationResult inventoryValidationResult,
                                                         OrderValidationResult orderDetailsValidationResult) {
        String orderId = "1";
        orderInputTopic.pipeInput(orderId, createOrder(orderId));
        orderValidationInputTopic.pipeRecordList(
                List.of(
                        new TestRecord<>(orderId, createValidation(orderId, OrderValidationType.FRAUD_CHECK, fraudValidationResult)),
                        new TestRecord<>(orderId, createValidation(orderId, OrderValidationType.INVENTORY_CHECK, inventoryValidationResult)),
                        new TestRecord<>(orderId, createValidation(orderId, OrderValidationType.ORDER_DETAILS_CHECK, orderDetailsValidationResult))
                )
        );

        Order acceptedOrder = orderOutputTopic.readKeyValue().value;
        assertThat(acceptedOrder.getId())
                .isEqualTo(orderId);
        assertThat(acceptedOrder.getState())
                .isEqualTo(OrderState.FAILED);
    }

    @Test
    void shouldWaitForAllValidations() {
        String orderId = "1";
        orderInputTopic.pipeInput(orderId, createOrder(orderId));
        orderValidationInputTopic.pipeRecordList(
                List.of(
                        new TestRecord<>(orderId, createPassedValidation(orderId, OrderValidationType.FRAUD_CHECK)),
                        new TestRecord<>(orderId, createPassedValidation(orderId, OrderValidationType.INVENTORY_CHECK))
                )
        );

        assertThat(orderOutputTopic.isEmpty())
                .isTrue();
    }
}