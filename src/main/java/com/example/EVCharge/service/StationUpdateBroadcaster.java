package com.example.EVCharge.service;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Sinks;

@Component
public class StationUpdateBroadcaster {

    private final Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();

    public void broadcastUpdate() {
        sink.tryEmitNext("update");
    }

    public Sinks.Many<String> getSink() {
        return sink;
    }
}