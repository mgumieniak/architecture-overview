package com.mgumieniak.architecture.webapp.kafka.topics;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;

import javax.annotation.CheckForNull;

@Value
@Builder
public class Topic<K, V> {
    @NonNull
    String name;

    @NonNull
    Produced<K, V> produced;

    @NonNull
    Consumed<K, V> consumed;

    @NonNull
    Serde<K> kSerde;

    @NonNull
    Serde<V> vSerde;
}
