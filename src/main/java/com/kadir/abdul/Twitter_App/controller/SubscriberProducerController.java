package com.kadir.abdul.Twitter_App.controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kadir.abdul.Twitter_App.dto.Subscribe;
import com.kadir.abdul.Twitter_App.response.ApiResponse;
import com.kadir.abdul.Twitter_App.service.SubscriberProducerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v2")
public class SubscriberProducerController {
    @Autowired
    private SubscriberProducerService subscriberProducerService;

    public SubscriberProducerController(SubscriberProducerService subscriberProducerService) {
        this.subscriberProducerService = subscriberProducerService;
    }

    @GetMapping("/{subscriberId}/producers")
    public CompletableFuture<ResponseEntity<List<Long>>> getProducersBySubscriber(@PathVariable Long subscriberId) {
        return subscriberProducerService.listProducerBySubscriber(subscriberId);
    }


    @PostMapping("/subscribe")
    public CompletableFuture<ResponseEntity<ApiResponse<String>>> subscribe(@RequestBody @Valid Subscribe request) {
        return subscriberProducerService.subscribe(request);
    }

}
