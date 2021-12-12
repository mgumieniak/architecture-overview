package com.mgumieniak.architecture.webapp.controllers;

import com.mgumieniak.architecture.models.Customer;
import com.mgumieniak.architecture.webapp.services.customer.CustomerService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomersController {

    private final CustomerService customerService;

    @PostMapping
    public void addCustomer(@RequestBody @NonNull final Customer customer){
        customerService.addCustomer(customer);
    }

}
