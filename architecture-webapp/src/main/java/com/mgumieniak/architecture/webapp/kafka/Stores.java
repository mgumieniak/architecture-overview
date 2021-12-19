package com.mgumieniak.architecture.webapp.kafka;


import com.mgumieniak.architecture.models.Customer;
import com.mgumieniak.architecture.models.orders.Order;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;

@Configuration
@RequiredArgsConstructor
public class Stores {
    public static final String DS_LATEST_ORDER_WITH_MONEY_SPENT = "latest-order-with-money-spent";
    public static final String DS_RESERVED_STOCK_STORE = "product-to-amount-in-warehouse";
    public static final String DS_CUSTOMERS_STORE = "customerId-to-customer";
    public static final String DS_ORDERS_STORE = "orders";
    private final StreamsBuilderFactoryBean streamsBuilderFactoryBean;

    @Lazy
    @Bean
    public ReadOnlyKeyValueStore<Long, Customer> getCustomerIdToCustomerGlobalStore() {
        return streamsBuilderFactoryBean.getKafkaStreams()
                .store(StoreQueryParameters.fromNameAndType(Stores.DS_CUSTOMERS_STORE, QueryableStoreTypes.keyValueStore()));
    }

    @Lazy
    @Bean
    public ReadOnlyKeyValueStore<String, Order> getOrderIdToOrderLocalStore(){
        return streamsBuilderFactoryBean.getKafkaStreams()
                .store(StoreQueryParameters.fromNameAndType(Stores.DS_ORDERS_STORE, QueryableStoreTypes.keyValueStore()));
    }

}

