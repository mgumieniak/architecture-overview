package com.mgumieniak.architecture.models;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.annotation.CheckForNull;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class Email {

    @NotNull
    private final Order order;

    @NotNull
    private final Payment payment;

    @CheckForNull
    private Customer customer;

    public Email setCustomerData(final Customer customer) {
        this.customer = customer;
        return this;
    }


}
