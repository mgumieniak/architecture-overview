package com.mgumieniak.architecture.webapp.controllers.customers;

import com.mgumieniak.architecture.models.Customer;
import com.mgumieniak.architecture.webapp.services.customer.CustomerService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomersController {

    private final CustomerService customerService;


    @PostMapping
    public void addCustomer(@RequestBody @NonNull final Customer customer) {
        customerService.addCustomer(customer);
    }

    @GetMapping("/{customerId}")
    public Mono<Customer> getCustomer(@PathVariable("customerId") long customerId) {
        return customerService.getCustomer(customerId);
    }

}
