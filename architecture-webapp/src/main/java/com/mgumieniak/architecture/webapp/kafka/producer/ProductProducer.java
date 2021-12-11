package com.mgumieniak.architecture.webapp.kafka.producer;

import com.mgumieniak.architecture.models.products.Product;
import com.mgumieniak.architecture.webapp.kafka.ProductSerializer;
import com.mgumieniak.architecture.webapp.kafka.ProductTypeSerde;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class ProductProducer {

    private final KafkaProperties kafkaProperties;

    private final Serde<Product> productSerde;

    @Bean
    public Map<String, Object> productProducerConfigs() {
        Map<String, Object> props =
                new HashMap<>(kafkaProperties.buildProducerProperties());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, ProductSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        props.put(ProducerConfig.RETRIES_CONFIG, "2");
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        return props;
    }

    @Bean
    public ProducerFactory<Product, Integer> productProducerFactory() {
        return new DefaultKafkaProducerFactory<>(productProducerConfigs());
    }

    @Bean
    public KafkaTemplate<Product, Integer> productKafkaTemplate() {
        return new KafkaTemplate<>(productProducerFactory());
    }
}
