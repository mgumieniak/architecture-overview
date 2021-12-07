package com.mgumieniak.architecture.webapp.services.inventory;

import com.mgumieniak.architecture.models.Order;
import com.mgumieniak.architecture.models.OrderState;
import com.mgumieniak.architecture.models.OrderValidation;
import com.mgumieniak.architecture.models.Product;
import com.mgumieniak.architecture.webapp.kafka.Topic;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.state.Stores;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

import static com.mgumieniak.architecture.webapp.kafka.Stores.DS_RESERVED_STOCK_STORE_NAME;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final Topic<String, Order> orderTopic;
    private final Topic<Product, Integer> productTopic;
    private final Topic<String, OrderValidation> orderValidationTopic;

    public void createProductToReservedStocks(final @NonNull StreamsBuilder builder) {
        val productToAmountStore = Stores
                .keyValueStoreBuilder(Stores.persistentKeyValueStore(DS_RESERVED_STOCK_STORE_NAME),
                        productTopic.getKSerde(), Serdes.Long())
                .withLoggingEnabled(new HashMap<>());
        builder.addStateStore(productToAmountStore);
    }

    @Autowired
    public void check(final @NonNull StreamsBuilder builder) {
        createProductToReservedStocks(builder);
        val customerIdToCreatedOrder = builder
                .stream(orderTopic.getName(), orderTopic.getConsumed())
                .filter((customerId, order) -> order.getState() == OrderState.CREATED)
                .peek((key, value) -> log.info("[customerIdToCreatedOrder] key: {}; value: {}", key, value));;


        val productToOrder = customerIdToCreatedOrder
                .selectKey((customerId, order) -> order.getProduct())
                .peek((key, value) -> log.info("[productToOrder] key: {}; value: {}", key, value));

        val productToStockAmountInWarehouse = builder
                .table(productTopic.getName(), productTopic.getConsumed());

        val productToOrderAndStockAmountInWarehouse = productToOrder
                .join(
                        productToStockAmountInWarehouse,
                        KeyValue::new,
                        Joined.with(productTopic.getKSerde(), orderTopic.getVSerde(), Serdes.Integer())
                );

        productToOrderAndStockAmountInWarehouse
                .transform(InventoryValidator::new, DS_RESERVED_STOCK_STORE_NAME)
                .to(orderValidationTopic.getName(), orderValidationTopic.getProduced());
    }
}
