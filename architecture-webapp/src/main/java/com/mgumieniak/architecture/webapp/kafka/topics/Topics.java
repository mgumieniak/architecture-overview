package com.mgumieniak.architecture.webapp.kafka.topics;

import com.mgumieniak.architecture.models.*;
import com.mgumieniak.architecture.models.orders.Order;
import com.mgumieniak.architecture.models.products.Product;
import com.mgumieniak.architecture.models.validations.OrderValidation;
import com.mgumieniak.architecture.webapp.kafka.serialization.OrderTimestampExtractor;
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
    public static String ORDERS_ENRICHED = "orders-enriched";
    public static String PAYMENTS = "payments";
    public static String CUSTOMERS = "customers";
    public static String WAREHOUSE_INVENTORY = "warehouse-inventory";
    public static String ORDER_VALIDATIONS = "order-validations";
    public static String EMAILS = "emails";

    private final Serde<Order> orderSerde;
    private final Serde<OrderValidation> orderValidationSerde;
    private final Serde<Product> productSerde;
    private final Serde<Payment> paymentSerde;
    private final Serde<Customer> customerSerde;
    private final Serde<Email> emailSerde;


    private final OrderTimestampExtractor orderTimestampExtractor;

    @Bean
    public Topic<String, Order> createOrderTopic() {
        return Topic.<String, Order>builder()
                .name(ORDERS)
                .kSerde(Serdes.String())
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
                .kSerde(Serdes.String())
                .vSerde(orderValidationSerde)
                .produced(Produced.with(Serdes.String(), orderValidationSerde))
                .consumed(
                        Consumed.with(Serdes.String(), orderValidationSerde))
                .build();
    }

    @Bean
    public Topic<Product, Integer> createWarehouseInventoryTopic() {
        return Topic.<Product, Integer>builder()
                .name(WAREHOUSE_INVENTORY)
                .kSerde(productSerde)
                .vSerde(Serdes.Integer())
                .produced(Produced.with(productSerde, Serdes.Integer()))
                .consumed(Consumed.with(productSerde, Serdes.Integer()))
                .build();
    }

    @Bean
    public Topic<String, Payment> createPaymentTopic() {
        return Topic.<String, Payment>builder()
                .name(PAYMENTS)
                .kSerde(Serdes.String())
                .vSerde(paymentSerde)
                .produced(Produced.with(Serdes.String(), paymentSerde))
                .consumed(Consumed.with(Serdes.String(), paymentSerde))
                .build();
    }

    @Bean
    public Topic<Long, Customer> createCustomerTopic() {
        return Topic.<Long, Customer>builder()
                .name(CUSTOMERS)
                .kSerde(Serdes.Long())
                .vSerde(customerSerde)
                .produced(Produced.with(Serdes.Long(), customerSerde))
                .consumed(Consumed.with(Serdes.Long(), customerSerde))
                .build();
    }


    @Bean
    public Topic<String, Email> createEmailTopic() {
        return Topic.<String, Email>builder()
                .name(EMAILS)
                .kSerde(Serdes.String())
                .vSerde(emailSerde)
                .produced(Produced.with(Serdes.String(), emailSerde))
                .consumed(Consumed.with(Serdes.String(), emailSerde))
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

    @Bean
    public NewTopic buildPaymentsTopic() {
        return TopicBuilder.name(PAYMENTS)
                .partitions(4)
                .replicas(1)
                .build();
    }


    @Bean
    public NewTopic buildCustomerTopic() {
        return TopicBuilder.name(CUSTOMERS)
                .partitions(4)
                .replicas(1)
                .build();
    }


    @Bean
    public NewTopic buildEmailTopic() {
        return TopicBuilder.name(EMAILS)
                .partitions(4)
                .replicas(1)
                .build();
    }
}
