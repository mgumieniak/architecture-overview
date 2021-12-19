package com.mgumieniak.architecture.webapp.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.KafkaStreams;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;

@Configuration
@RequiredArgsConstructor
public class KafkaStreamCreator {

    private final StreamsBuilderFactoryBean streamsBuilderFactoryBean;

    @Bean
    public KafkaStreams createKafkaStreams() {
        return streamsBuilderFactoryBean.getKafkaStreams();
    }
}


