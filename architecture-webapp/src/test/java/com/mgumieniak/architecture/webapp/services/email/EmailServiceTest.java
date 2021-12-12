package com.mgumieniak.architecture.webapp.services.email;

import com.mgumieniak.architecture.models.Customer;
import com.mgumieniak.architecture.models.Email;
import com.mgumieniak.architecture.models.Order;
import com.mgumieniak.architecture.models.Payment;
import com.mgumieniak.architecture.webapp.BasicStreamTest;
import com.mgumieniak.architecture.webapp.kafka.topics.Topic;
import org.apache.kafka.streams.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.mgumieniak.architecture.webapp.ObjectCreator.createOrder;
import static org.assertj.core.api.Assertions.assertThat;

class EmailServiceTest extends BasicStreamTest {

    private TestInputTopic<String, Order> orderTestInputTopic;
    private TestInputTopic<String, Payment> paymentTestInputTopic;
    private TestInputTopic<Long, Customer> customerTestInputTopic;
    private TestOutputTopic<String, Email> emailTestOutputTopic;

    private final Topic<String, Order> orderTopic = topics.createOrderTopic();
    private final Topic<String, Payment> paymentTopic = topics.createPaymentTopic();
    private final Topic<Long, Customer> customerTopic = topics.createCustomerTopic();
    private final Topic<String, Email> emailTopic = topics.createEmailTopic();


    EmailService emailService = new EmailService(orderTopic, paymentTopic, customerTopic, emailTopic);

    @BeforeEach
    void setUp() {
        StreamsBuilder builder = new StreamsBuilder();
        emailService.sendEmail(builder);
        testDriver = new TopologyTestDriver(builder.build(), streamProperties);

        orderTestInputTopic = createInputTopic(orderTopic);
        paymentTestInputTopic = createInputTopic(paymentTopic);
        customerTestInputTopic = createInputTopic(customerTopic);
        emailTestOutputTopic = createOutputTopic(emailTopic);
    }

    @AfterEach
    void tearDown() {
        testDriver.close();
    }

    @Test
    void shouldSendEmailEvent() {
        String orderId = "1";
        Long customerId = 2L;
        Order order = createOrder(orderId, customerId);
        Payment payment = createPayment(orderId);
        Customer customer = createCustomer(customerId);

        customerTestInputTopic.pipeInput(customerId, customer);
        orderTestInputTopic.pipeInput(orderId, order);
        paymentTestInputTopic.pipeInput(orderId, payment);

        KeyValue<String, Email> orderIdToEmail = emailTestOutputTopic.readKeyValue();
        assertThat(orderIdToEmail.key).isEqualTo(orderId);
        assertThat(orderIdToEmail.value).isEqualTo(Email.builder()
                .order(order)
                .payment(payment)
                .customer(customer)
                .build());
    }

    private Customer createCustomer(Long customerId) {
        return Customer.builder()
                .id(customerId)
                .firstName("Jan")
                .lastName("Kowal")
                .email("jan.kowal@gmail.com")
                .address("Wawelska 19b 31-389 Cracow")
                .build();
    }

    private Payment createPayment(String orderId) {
        return Payment.builder()
                .id("2")
                .orderId(orderId)
                .amount(12.00)
                .ccy("129")
                .build();
    }
}