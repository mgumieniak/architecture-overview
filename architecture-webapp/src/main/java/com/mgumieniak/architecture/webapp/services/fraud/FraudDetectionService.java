package com.mgumieniak.architecture.webapp.services.fraud;

import com.mgumieniak.architecture.models.orders.OrderValue;
import lombok.NonNull;
import org.springframework.stereotype.Service;

@Service
public class FraudDetectionService {
    private static final int FRAUD_LIMIT = 2000;


    public boolean isMoneyLimitExceeded(final @NonNull OrderValue orderValue){
        return orderValue.getValue() >= FRAUD_LIMIT;
    }

    public boolean isMoneyBelowLimit(final @NonNull OrderValue orderValue){
        return !isMoneyLimitExceeded(orderValue);
    }
}
