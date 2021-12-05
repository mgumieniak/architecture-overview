package com.mgumieniak.architecture.webapp.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class Topics {
    public static String ORDERS = "orders";
    public static String ORDERS_ENRICHED = "orders-enriched";
    public static String PAYMENTS = "payments";
    public static String CUSTOMERS = "customers";
    public static String WAREHOUSE_INVENTORY = "warehouse-inventory";
    public static String ORDER_VALIDATIONS = "order-validations";

    @Bean
    public NewTopic buildOrdersTopic(){
        return TopicBuilder.name(ORDERS)
                .partitions(4)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic buildOrderValidationsTopic(){
        return TopicBuilder.name(ORDER_VALIDATIONS)
                .partitions(4)
                .replicas(1)
                .build();
    }

}
