package com.mgumieniak.architecture.webapp.services.customer;

import com.mgumieniak.architecture.models.Customer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.mgumieniak.architecture.webapp.kafka.topics.Topics.CUSTOMERS;

@Service
@RequiredArgsConstructor
public class CustomerService {

    @Qualifier("customerKafkaTemplate")
    private final KafkaTemplate<Long, Object> kafkaTemplate;


    public void addCustomer(@NonNull final Customer customer) {
        kafkaTemplate.send(CUSTOMERS, customer.getId(), customer);
    }

}
