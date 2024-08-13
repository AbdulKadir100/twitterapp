package com.kadir.abdul.Twitter_App.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;


public interface SubscriberProducerService {

    ResponseEntity<CompletableFuture<List<Long>>> listProducerBySubscriber(Long subscriberId);

}
