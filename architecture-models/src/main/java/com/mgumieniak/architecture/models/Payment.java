package com.mgumieniak.architecture.models;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Payment {
    String id;
    String orderId;
    String ccy;
    double amount;
}
