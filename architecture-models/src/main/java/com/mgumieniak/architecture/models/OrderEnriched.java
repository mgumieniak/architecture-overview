package com.mgumieniak.architecture.models;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OrderEnriched {
    String id;
    long customerId;
    String customerLevel;
}
