package com.mgumieniak.architecture.webapp.kafka.exception.entry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DeserializationExceptionHandler {

    @Qualifier("kafkaTemplate")
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Bean("dlqDeserializationPublishingRecoverer")
    public DeadLetterPublishingRecoverer publishToDLQ() {
        return new DeadLetterPublishingRecoverer(kafkaTemplate,
                (record, ex) -> new TopicPartition("deserializationDLQ", -1));
    }
}
