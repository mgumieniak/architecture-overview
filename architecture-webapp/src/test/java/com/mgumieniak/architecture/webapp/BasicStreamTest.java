package com.mgumieniak.architecture.webapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mgumieniak.architecture.webapp.kafka.serialization.JsonSerdes;
import com.mgumieniak.architecture.webapp.kafka.serialization.OrderTimestampExtractor;
import com.mgumieniak.architecture.webapp.kafka.topics.Topic;
import com.mgumieniak.architecture.webapp.kafka.topics.Topics;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;
import org.springframework.kafka.support.JacksonUtils;

import java.util.Objects;
import java.util.Properties;

import static org.apache.kafka.streams.StreamsConfig.EXACTLY_ONCE_V2;

public abstract class BasicStreamTest {

    public TopologyTestDriver testDriver;
    public final Properties streamProperties;
    public final Topics topics;
    public final JsonSerdes jsonSerdes;
    public final ObjectMapper mapper = createObjectMapper();

    public BasicStreamTest() {
        this.streamProperties = getKafkaStreamProperties();
        this.jsonSerdes = new JsonSerdes(mapper);
        this.topics = createTopics();
    }

    public <K, V> TestInputTopic<K, V> createInputTopic(Topic<K, V> topic) {
        return testDriver.createInputTopic(
                topic.getName(),
                Objects.requireNonNull(topic.getKSerde()).serializer(),
                topic.getVSerde().serializer()
        );
    }

    public <K, V> TestOutputTopic<K, V> createOutputTopic(Topic<K, V> topic) {
        return testDriver.createOutputTopic(
                topic.getName(),
                Objects.requireNonNull(topic.getKSerde()).deserializer(),
                topic.getVSerde().deserializer()
        );
    }

    private ObjectMapper createObjectMapper() {
        ObjectMapper mapper = JacksonUtils.enhancedObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    private Properties getKafkaStreamProperties() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "test");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");
        props.put(StreamsConfig.CLIENT_ID_CONFIG, "test-architecture-def-stream-client");

        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class.getName());


        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 5);
        props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, EXACTLY_ONCE_V2);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        props.put(StreamsConfig.producerPrefix(ProducerConfig.ACKS_CONFIG), "all");

        return props;
    }

    private Topics createTopics() {
        OrderTimestampExtractor orderTimestampExtractor = new OrderTimestampExtractor();

        return new Topics(jsonSerdes.buildOrderSerde(), jsonSerdes.buildOrderValidationSerde(),
                jsonSerdes.buildProductSerde(), jsonSerdes.buildPaymentSerde(), jsonSerdes.buildCustomerSerde(),
                jsonSerdes.buildEmailSerde(), orderTimestampExtractor);
    }


}
