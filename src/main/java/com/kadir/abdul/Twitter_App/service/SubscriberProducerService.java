package com.kadir.abdul.Twitter_App.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import com.kadir.abdul.Twitter_App.dto.Subscribe;
import com.kadir.abdul.Twitter_App.response.ApiResponse;


public interface SubscriberProducerService {

    CompletableFuture<ResponseEntity<List<Long>>> listProducerBySubscriber(Long subscriberId);
    
    CompletableFuture<ResponseEntity<ApiResponse<String>>> subscribe(@RequestBody Subscribe request);

}
