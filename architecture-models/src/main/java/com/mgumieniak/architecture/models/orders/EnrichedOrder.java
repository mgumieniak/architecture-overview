package com.mgumieniak.architecture.models.orders;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class EnrichedOrder {
    String id;
    long customerId;
    String customerLevel;
}
