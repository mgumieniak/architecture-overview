package com.mgumieniak.architecture.webapp.services.customer;

import com.mgumieniak.architecture.models.Customer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import static com.mgumieniak.architecture.webapp.kafka.topics.Topics.CUSTOMERS;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

    @Qualifier("customerKafkaTemplate")
    private final KafkaTemplate<Long, Object> kafkaTemplate;
    @Lazy
    private final ReadOnlyKeyValueStore<Long, Customer> customerIdToCustomerGlobalStore;


    public void addCustomer(@NonNull final Customer customer) {
        kafkaTemplate.send(CUSTOMERS, customer.getId(), customer);
    }

    public Mono<Customer> getCustomer(long customerId) {
        return Mono.fromCallable(() -> customerIdToCustomerGlobalStore.get(customerId))
                .subscribeOn(Schedulers.boundedElastic());
    }

}
