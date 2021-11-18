package com.mgumieniak.architecture.models;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class OrderValidation {
    String orderId;
    OrderValidationType checkType;
    OrderValidationResult validationResult;
}
