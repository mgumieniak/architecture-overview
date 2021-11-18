package com.mgumieniak.architecture.models;

import lombok.Builder;

import java.time.Instant;

public interface Timestamp {
    Instant getTimestamp();
}
