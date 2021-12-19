package com.mgumieniak.architecture.webapp.services.order;

import com.mgumieniak.architecture.connectors.OrderClient;
import com.mgumieniak.architecture.models.orders.Order;
import com.mgumieniak.architecture.models.orders.OrderDTO;
import com.mgumieniak.architecture.webapp.kafka.topics.Topic;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyQueryMetadata;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.HostInfo;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.URI;
import java.util.Objects;

import static com.mgumieniak.architecture.models.orders.OrderDTO.fromOrderDTO;
import static com.mgumieniak.architecture.webapp.kafka.Stores.DS_ORDERS_STORE;

@Service
@RequiredArgsConstructor
public class OrderService {

    @Qualifier("txKafkaTemplate")
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Lazy
    private final ReadOnlyKeyValueStore<String, Order> orderIdToOrderLocalStore;
    private final Topic<String, Order> orderTopic;
    private final StreamsBuilderFactoryBean streamsBuilderFactoryBean;
    private final HostInfo hostInfo;
    private final OrderClient orderClient;

    @Transactional
    public void createOrder(@NonNull final OrderDTO orderDTO) {
        final Order order = fromOrderDTO(orderDTO);
        kafkaTemplate.send(orderTopic.getName(), String.valueOf(order.getId()), order);
    }

    @Autowired
    public void createOrderDataStore(final @NonNull StreamsBuilder builder) {
        builder
                .stream(orderTopic.getName(), orderTopic.getConsumed())
                .toTable(Materialized.<String, Order, KeyValueStore<Bytes, byte[]>>
                        as(DS_ORDERS_STORE)
                        .withKeySerde(orderTopic.getKSerde())
                        .withValueSerde(orderTopic.getVSerde()));
    }

    public Mono<OrderDTO> getLocallyOrder(@NonNull final String orderId) {
        return Mono.fromCallable(() -> orderIdToOrderLocalStore.get(orderId))
                .subscribeOn(Schedulers.boundedElastic())
                .map(OrderDTO::toOrderDTO);
    }

    public Mono<OrderDTO> getOrder(@NonNull final String id) {
        val kafkaStream = streamsBuilderFactoryBean.getKafkaStreams();

        return Mono.fromCallable(() -> kafkaStream
                .queryMetadataForKey(DS_ORDERS_STORE, id, Objects.requireNonNull(orderTopic.getKSerde()).serializer()))
                .map(KeyQueryMetadata::activeHost)
                .flatMap(hostInfoWhichContainKey -> {
                    if (isDataLocallyAvailable(hostInfoWhichContainKey)) {
                        return getLocallyOrder(id);
                    } else {
                        val uri = URI.create("http://" + hostInfoWhichContainKey.host() + ":" + hostInfoWhichContainKey.port());
                        return orderClient.getOrder(uri, id);
                    }
                });
    }

    private boolean isDataLocallyAvailable(HostInfo hostInfoWhichContainKey) {
        return hostInfoWhichContainKey.equals(this.hostInfo);
    }
}
