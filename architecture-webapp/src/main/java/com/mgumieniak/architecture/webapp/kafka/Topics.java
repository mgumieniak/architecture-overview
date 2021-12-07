package com.mgumieniak.architecture.webapp.kafka;

import com.mgumieniak.architecture.models.Order;
import com.mgumieniak.architecture.models.OrderValidation;
import com.mgumieniak.architecture.models.Product;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@RequiredArgsConstructor
public class Topics {
    public static String ORDERS = "orders";
    private static String ORDERS_ENRICHED = "orders-enriched";
    private static String PAYMENTS = "payments";
    private static String CUSTOMERS = "customers";
    private static String WAREHOUSE_INVENTORY = "warehouse-inventory";
    public static String ORDER_VALIDATIONS = "order-validations";

    private final Serde<Order> orderSerde;
    private final Serde<OrderValidation> orderValidationSerde;
    private final Serde<Product> productSerde;

    private final OrderTimestampExtractor orderTimestampExtractor;

    @Bean
    public Topic<String, Order> createOrderTopic() {
        return Topic.<String, Order>builder()
                .name(ORDERS)
                .vSerde(orderSerde)
                .produced(Produced.with(Serdes.String(), orderSerde))
                .consumed(
                        Consumed.with(Serdes.String(), orderSerde)
                                .withTimestampExtractor(orderTimestampExtractor))
                .build();
    }

    @Bean
    public Topic<String, OrderValidation> createOrderValidationTopic() {
        return Topic.<String, OrderValidation>builder()
                .name(ORDER_VALIDATIONS)
                .vSerde(orderValidationSerde)
                .produced(Produced.with(Serdes.String(), orderValidationSerde))
                .consumed(
                        Consumed.with(Serdes.String(), orderValidationSerde)
                                .withTimestampExtractor(orderTimestampExtractor))
                .build();
    }

    @Bean
    public Topic<Product, Integer> createWarehouseInventoryTopic() {
        return Topic.<Product, Integer>builder()
                .name(WAREHOUSE_INVENTORY)
                .kSerde(productSerde)
                .vSerde(Serdes.Integer())
                .produced(Produced.with(productSerde, Serdes.Integer()))
                .consumed(
                        Consumed.with(productSerde, Serdes.Integer())
                                .withTimestampExtractor(orderTimestampExtractor))
                .build();
    }


    @Bean
    public NewTopic buildOrdersTopic() {
        return TopicBuilder.name(ORDERS)
                .partitions(4)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic buildOrderValidationsTopic() {
        return TopicBuilder.name(ORDER_VALIDATIONS)
                .partitions(4)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic buildWarehouseInventoryTopic() {
        return TopicBuilder.name(WAREHOUSE_INVENTORY)
                .partitions(4)
                .replicas(1)
                .build();
    }
}
