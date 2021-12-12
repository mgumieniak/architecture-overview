package com.mgumieniak.architecture.webapp.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class CustomerProducer {

    private final KafkaProperties kafkaProperties;

    @Bean
    public Map<String, Object> customerProducerConfigs() {
        Map<String, Object> props =
                new HashMap<>(kafkaProperties.buildProducerProperties());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        props.put(ProducerConfig.RETRIES_CONFIG, "2");
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        return props;
    }

    @Bean
    public ProducerFactory<Long, Object> customerProducerFactory() {
        return new DefaultKafkaProducerFactory<>(customerProducerConfigs());
    }

    @Bean
    public KafkaTemplate<Long, Object> customerKafkaTemplate() {
        return new KafkaTemplate<>(customerProducerFactory());
    }
}
