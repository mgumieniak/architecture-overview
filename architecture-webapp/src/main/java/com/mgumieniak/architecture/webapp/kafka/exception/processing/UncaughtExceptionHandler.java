package com.mgumieniak.architecture.webapp.kafka.exception.processing;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UncaughtExceptionHandler implements StreamsUncaughtExceptionHandler {

    @Override
    public StreamThreadExceptionResponse handle(Throwable exception) {
        log.error("[UncaughtExceptionHandler] Catch exception: {}", exception.toString());
        return StreamThreadExceptionResponse.REPLACE_THREAD;
    }
}