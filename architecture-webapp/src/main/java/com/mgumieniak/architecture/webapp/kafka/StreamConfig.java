package com.mgumieniak.architecture.webapp.kafka;

import com.mgumieniak.architecture.webapp.kafka.exception.processing.UncaughtExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;
import org.apache.kafka.streams.state.HostInfo;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.autoconfigure.kafka.StreamsBuilderFactoryBeanCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.KafkaStreamsCustomizer;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.streams.RecoveringDeserializationExceptionHandler;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.streams.StreamsConfig.EXACTLY_ONCE_V2;

@Slf4j
@Configuration
@EnableKafka
@EnableKafkaStreams
@RequiredArgsConstructor
public class StreamConfig {

    @Qualifier("dlqDeserializationPublishingRecoverer")
    private final DeadLetterPublishingRecoverer dlqDesPublishingRecoverer;
    private final KafkaProperties kafkaProperties;
    private final UncaughtExceptionHandler uncaughtExceptionHandler;

    @Value("${state-dir}")
    private final String stateDir;

    @Value("${server.host}")
    private final String host;

    @Value("${server.port}")
    private final String port;


    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration defaultKafkaStreamsConfig() {
        log.info("INSIDE defaultKafkaStreamsConfig: stateDir: {}, host: {}, port: {}", stateDir, host, port);
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildStreamsProperties());
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "architecture-def-stream");
        props.put(StreamsConfig.CLIENT_ID_CONFIG, "architecture-def-stream-client");

        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class.getName());

        props.put(StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG, RecoveringDeserializationExceptionHandler.class);
        props.put(RecoveringDeserializationExceptionHandler.KSTREAM_DESERIALIZATION_RECOVERER, dlqDesPublishingRecoverer);


        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 5);
        props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, EXACTLY_ONCE_V2);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(StreamsConfig.STATE_DIR_CONFIG, stateDir);
//        props.put(StreamsConfig.REPLICATION_FACTOR_CONFIG, 3);                         // TODO: change to 3 when nbBroker >= 3

        props.put(StreamsConfig.producerPrefix(ProducerConfig.ACKS_CONFIG), "all");

        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "20000");
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, "6000");

        props.put(StreamsConfig.APPLICATION_SERVER_CONFIG, host + ":" + port);

        return new KafkaStreamsConfiguration(props);
    }


    @Bean
    public StreamsBuilderFactoryBeanCustomizer streamsBuilderFactoryBeanCustomizer() {
        return factoryBean -> factoryBean.
                setKafkaStreamsCustomizer(
                        kafkaStreams -> kafkaStreams.setUncaughtExceptionHandler(uncaughtExceptionHandler)
                );
    }

    @Bean
    HostInfo getHostInfo(){
        return new HostInfo(host, Integer.parseInt(port));
    }

}
