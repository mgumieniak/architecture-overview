package com.mgumieniak.architecture.webapp.services.email;

import com.mgumieniak.architecture.models.Customer;
import com.mgumieniak.architecture.models.Email;
import com.mgumieniak.architecture.models.Order;
import com.mgumieniak.architecture.models.Payment;
import com.mgumieniak.architecture.webapp.kafka.topics.Topic;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.SessionStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final Topic<String, Order> orderTopic;
    private final Topic<String, Payment> paymentTopic;
    private final Topic<Long, Customer> customerTopic;
    private final Topic<String, Email> emailTopic;

    @Autowired
    public void sendEmail(final @NonNull StreamsBuilder builder) {
        final KStream<String, Order> orderIdToOrder = builder.stream(orderTopic.getName(), orderTopic.getConsumed());
        final KStream<String, Payment> orderIdToPayment = builder.stream(paymentTopic.getName(), paymentTopic.getConsumed());
        final GlobalKTable<Long, Customer> customerIdToCustomer = builder
                .globalTable(customerTopic.getName(),
                        Materialized.<Long, Customer, KeyValueStore<Bytes, byte[]>>as("customerDS")
                                .withKeySerde(customerTopic.getKSerde())
                                .withValueSerde(customerTopic.getVSerde()));

        val orderIdToEmail = orderIdToOrder.join(
                orderIdToPayment,
                (order, payment) -> Email.builder().order(order).payment(payment).build(),
                JoinWindows.of(Duration.ofHours(1)),
                StreamJoined.with(orderTopic.getKSerde(), orderTopic.getVSerde(), paymentTopic.getVSerde())
        ).peek((key, value) -> log.info("[orderIdToEmail] key: {}, value: {}", key, value));

        orderIdToEmail.join(
                customerIdToCustomer,
                (orderId, email) -> {
                    log.info("ORDER_ID: {}, EMAIL: {}", orderId, email);
                    return email.getOrder().getCustomerId();
                },
                (email1, customer) -> {
                    log.info("CUSTOMER in join: {}", customer);
                    return email1.setCustomerData(customer);
                })
                .to(emailTopic.getName(), emailTopic.getProduced());
    }

}
