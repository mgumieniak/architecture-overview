package com.mgumieniak.architecture.webapp.services.payments;

import com.mgumieniak.architecture.models.Payment;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.mgumieniak.architecture.webapp.kafka.topics.Topics.PAYMENTS;

@Service
@RequiredArgsConstructor
public class PaymentService {

    @Qualifier("kafkaTemplate")
    private final KafkaTemplate<String, Object> kafkaTemplate;


    public void place(@NonNull final Payment payment) {
        kafkaTemplate.send(PAYMENTS, payment.getOrderId(), payment);
    }
}
