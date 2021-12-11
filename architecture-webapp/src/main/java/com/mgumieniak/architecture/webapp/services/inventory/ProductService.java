package com.mgumieniak.architecture.webapp.services.inventory;

import com.mgumieniak.architecture.models.products.Product;
import com.mgumieniak.architecture.models.products.ProductSupplyRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.mgumieniak.architecture.webapp.kafka.Topics.WAREHOUSE_INVENTORY;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    @Qualifier("productKafkaTemplate")
    private final KafkaTemplate<Product, Integer> kafkaTemplate;

    public void add(@NonNull final ProductSupplyRequest productSupplyRequest) {
        kafkaTemplate.send(WAREHOUSE_INVENTORY, productSupplyRequest.getProduct(), productSupplyRequest.getAmount());
    }
}
