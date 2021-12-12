package com.mgumieniak.architecture.models;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Value
@Builder
public class Payment {

    @NotNull
    String id;

    @NotNull
    String orderId;

    @NotNull
    String ccy;

    @Min(value = 0)
    double amount;
}
