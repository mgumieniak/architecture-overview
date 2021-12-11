package com.mgumieniak.architecture.models;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import static com.mgumieniak.architecture.models.OrderValidationResult.PASS;
import static com.mgumieniak.architecture.models.OrderValidationResult.UNDEFINED;

@Data
@Builder
public class OrderValidationResults {
    @Builder.Default
    OrderValidationResult fraudValidation = UNDEFINED;

    @Builder.Default
    OrderValidationResult inventoryValidation = UNDEFINED;

    @Builder.Default
    OrderValidationResult orderDetailsValidation = UNDEFINED;

    public boolean isDefined() {
        return fraudValidation != UNDEFINED && inventoryValidation != UNDEFINED &&
                orderDetailsValidation != UNDEFINED;
    }

    public boolean isPassed() {
        return fraudValidation == PASS && inventoryValidation == PASS &&
                orderDetailsValidation == PASS;
    }

    public boolean isNotPassed() {
        return !isPassed();
    }

    public static OrderValidationResults changeValidationResult(@NonNull final OrderValidationResults existedResult,
                                                                @NonNull final OrderValidation validation) {
        switch (validation.getCheckType()) {
            case FRAUD_CHECK:
                existedResult.setFraudValidation(validation.getValidationResult());
                return existedResult;
            case INVENTORY_CHECK:
                existedResult.setInventoryValidation(validation.getValidationResult());
                return existedResult;
            case ORDER_DETAILS_CHECK:
                existedResult.setOrderDetailsValidation(validation.getValidationResult());
                return existedResult;
            default:
                throw new IllegalArgumentException();
        }
    }
}
