package com.kadir.abdul.Twitter_App.service.Imlp;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.kadir.abdul.Twitter_App.repository.SubscriberProducerRepository;
import com.kadir.abdul.Twitter_App.service.SubscriberProducerService;

@Service
public class SubscriberProducerServiceImpl implements SubscriberProducerService {
    private SubscriberProducerRepository producerRepository;

    private final ExecutorService executor = Executors.newFixedThreadPool(10); // Customize the thread pool size as
                                                                               // needed

    public SubscriberProducerServiceImpl(SubscriberProducerRepository producerRepository) {
        this.producerRepository = producerRepository;
    }

    @Override
    public ResponseEntity<CompletableFuture<List<Long>>> listProducerBySubscriber(Long subscriberId) {
        // Asynchronously fetch the list of producer IDs
        CompletableFuture<List<Long>> futureProducerIds = CompletableFuture.supplyAsync(() -> {
            return producerRepository.listProducerBySubscriber(subscriberId);
        }, executor);
        return ResponseEntity.ok(futureProducerIds);

    }

}
