package com.mgumieniak.architecture.webapp.kafka;

import com.mgumieniak.architecture.models.Timestamp;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;
import org.springframework.stereotype.Service;

@Service
public class OrderTimestampExtractor implements TimestampExtractor {
    @Override
    public long extract(ConsumerRecord<Object, Object> record, long partitionTime) {
        Timestamp timestamp = (Timestamp) record.value();

        if (isTimestampAvailableInRecord(timestamp)) {
            return timestamp.getTimestamp().toEpochMilli();
        }

        return partitionTime;
    }

    private boolean isTimestampAvailableInRecord(Timestamp timestamp) {
        return timestamp != null && timestamp.getTimestamp() != null;
    }
}
