package com.mgumieniak.architecture.webapp.services.order.details;

import com.mgumieniak.architecture.models.orders.Order;
import com.mgumieniak.architecture.models.validations.OrderValidation;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.mgumieniak.architecture.models.validations.OrderValidationResult.FAIL;
import static com.mgumieniak.architecture.models.validations.OrderValidationResult.PASS;
import static com.mgumieniak.architecture.models.validations.OrderValidationType.ORDER_DETAILS_CHECK;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderDetailsCheckService {

    @NonNull
    public OrderValidation createOrderValidation(final @NonNull Order order) {
        if (order.getQuantity() < 0 || order.getPrice() < 0.0) {
            return new OrderValidation(order.getId(), ORDER_DETAILS_CHECK, FAIL);
        } else {
            return new OrderValidation(order.getId(), ORDER_DETAILS_CHECK, PASS);
        }
    }

}
