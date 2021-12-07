package com.mgumieniak.architecture.webapp.services.inventory;

import com.mgumieniak.architecture.models.Order;
import com.mgumieniak.architecture.models.OrderValidation;
import com.mgumieniak.architecture.models.Product;
import lombok.NonNull;
import lombok.val;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;

import static com.mgumieniak.architecture.models.OrderValidationResult.FAIL;
import static com.mgumieniak.architecture.models.OrderValidationResult.PASS;
import static com.mgumieniak.architecture.models.OrderValidationType.INVENTORY_CHECK;
import static com.mgumieniak.architecture.webapp.kafka.Stores.DS_RESERVED_STOCK_STORE_NAME;

public class InventoryValidator implements Transformer<Product, KeyValue<Order, Integer>, KeyValue<String, OrderValidation>> {

    private KeyValueStore<Product, Long> reservedStocksStore;

    @Override
    public void init(final ProcessorContext context) {
        reservedStocksStore = context.getStateStore(DS_RESERVED_STOCK_STORE_NAME);
    }

    @Override
    public KeyValue<String, OrderValidation> transform(final @NonNull Product product,
                                                       final @NonNull KeyValue<Order, Integer> orderAndStock) {
        val order = orderAndStock.key;
        val stockAmountInWarehouse = orderAndStock.value;
        val reservedNbStocks = getReservedNbStocks(product);

        val orderValidation = validate(order, stockAmountInWarehouse, reservedNbStocks);

        return KeyValue.pair(orderValidation.getOrderId(), orderValidation);
    }

    private Long getReservedNbStocks(Product product) {
        val reservedNbStocks = reservedStocksStore.get(product);
        return reservedNbStocks == null ? 0L : reservedNbStocks;
    }

    private OrderValidation validate(Order order, Integer stockAmountInWarehouse, Long reservedNbStocks) {
        final OrderValidation validated;
        if (isEnoughStock(order, stockAmountInWarehouse, reservedNbStocks)) {
            reservedStocksStore.put(order.getProduct(), reservedNbStocks + order.getQuantity());
            validated = new OrderValidation(order.getId(), INVENTORY_CHECK, PASS);
        } else {
            validated = new OrderValidation(order.getId(), INVENTORY_CHECK, FAIL);
        }
        return validated;
    }

    private boolean isEnoughStock(Order order, Integer warehouseStockCount, Long reservedNbStocks) {
        return warehouseStockCount - reservedNbStocks - order.getQuantity() >= 0;
    }

    @Override
    public void close() {
    }
}
