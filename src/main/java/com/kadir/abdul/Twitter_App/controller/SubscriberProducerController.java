package com.kadir.abdul.Twitter_App.controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kadir.abdul.Twitter_App.service.SubscriberProducerService;

@RestController
@RequestMapping("/api/v2")
public class SubscriberProducerController {
    private SubscriberProducerService subscriberProducerService;

    public SubscriberProducerController(SubscriberProducerService subscriberProducerService) {
        this.subscriberProducerService = subscriberProducerService;
    }

    @GetMapping("/{subscriberId}/producers")
    public ResponseEntity<CompletableFuture<List<Long>>> getProducersBySubscriber(@PathVariable Long subscriberId) {
        return subscriberProducerService.listProducerBySubscriber(subscriberId);
    }

}
