package com.example.EVCharge.controllers;

import com.example.EVCharge.service.StationUpdateBroadcaster;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/sse")
public class StationSseController {

    private final StationUpdateBroadcaster broadcaster;

    public StationSseController(StationUpdateBroadcaster broadcaster) {
        this.broadcaster = broadcaster;
    }

    @GetMapping("/updates")
    public Flux<ServerSentEvent<String>> streamUpdates() {
        return broadcaster.getSink()
                .asFlux()
                .map(data -> ServerSentEvent.builder(data).build());
    }
}