package com.kadir.abdul.Twitter_App.service.Imlp;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.kadir.abdul.Twitter_App.dto.Subscribe;
import com.kadir.abdul.Twitter_App.dto.UserDto;
import com.kadir.abdul.Twitter_App.entity.SubscriberProducer;
import com.kadir.abdul.Twitter_App.repository.SubscriberProducerRepository;
import com.kadir.abdul.Twitter_App.response.ApiResponse;
import com.kadir.abdul.Twitter_App.service.SubscriberProducerService;
import com.kadir.abdul.Twitter_App.service.UserService;
import com.kadir.abdul.Twitter_App.utils.MessageUtil;

@Service
public class SubscriberProducerServiceImpl implements SubscriberProducerService {
        @Autowired
        private SubscriberProducerRepository producerRepository;
        @Autowired
        private UserService userService;

        private static final Logger log = LoggerFactory.getLogger(SubscriberProducerServiceImpl.class);

        // private final ExecutorService executor = Executors.newFixedThreadPool(5); //
        // Customize the thread pool size as
        // needed

        public SubscriberProducerServiceImpl(SubscriberProducerRepository producerRepository, UserService userService) {
                this.producerRepository = producerRepository;
                this.userService = userService;
        }

        @Async
        @Override
        public CompletableFuture<ResponseEntity<List<Long>>> listProducerBySubscriber(Long subscriberId) {
                return producerRepository.listProducerBySubscriber(subscriberId)
                                .thenApply(producerIds -> ResponseEntity.ok(producerIds));
        }

        @SuppressWarnings("unchecked")
        @Async
        @Override
        public CompletableFuture<ResponseEntity<ApiResponse<String>>> subscribe(Subscribe request) {

                Long subscriberId = request.getSubscriberID();
                Long producerId = request.getUserId();

                log.info("Subscriber ID: {}", subscriberId);
                log.info("Producer ID: {}", producerId);

                // Check if both subscriber and producer exist
                CompletableFuture<ResponseEntity<ApiResponse<UserDto>>> subscriberFuture = userService
                                .findById(subscriberId);
                CompletableFuture<ResponseEntity<ApiResponse<UserDto>>> producerFuture = userService
                                .findById(producerId);

                return CompletableFuture.allOf(subscriberFuture, producerFuture)
                                .thenCompose(ignored -> {
                                        ResponseEntity<ApiResponse<UserDto>> subscriberResponse = subscriberFuture
                                                        .join();
                                        ResponseEntity<ApiResponse<UserDto>> producerResponse = producerFuture
                                                        .join();

                                        ApiResponse<UserDto> subscriberApiResponse = subscriberResponse.getBody();
                                        ApiResponse<UserDto> producerApiResponse = producerResponse.getBody();

                                        if (subscriberApiResponse == null || producerApiResponse == null ||
                                                        subscriberApiResponse.getData() == null
                                                        || producerApiResponse.getData() == null) {
                                                return CompletableFuture.completedFuture(
                                                                ResponseEntity.ok(new ApiResponse<>(MessageUtil.FAIL,
                                                                                HttpStatus.NOT_FOUND.value(),
                                                                                MessageUtil.RECORD_NOT_FOUND)));
                                        }

                                else if (producerApiResponse.getData() == null
                                                || producerApiResponse.getData().getURole() == null ||
                                                !"Producer".equalsIgnoreCase(
                                                                producerApiResponse.getData().getURole())) {
                                                log.info("Producer role is either null or not 'Producer': "
                                                                + producerApiResponse.getData().getURole());
                                                return CompletableFuture.completedFuture(
                                                                ResponseEntity.ok(
                                                                                new ApiResponse<>(MessageUtil.FAIL,
                                                                                                HttpStatus.FORBIDDEN
                                                                                                                .value(),
                                                                                                MessageUtil.SUBSCRIPTION_NOT_ALLOW)));
                                        }

                                        // Validate that a subscriber cannot make multiple subscriptions for the same
                                        // producer
                                        return producerRepository.checkSubscriptionStatus(subscriberId, producerId)
                                                        .thenCompose(existingSubscription -> {
                                                                if (existingSubscription != null) {
                                                                        return CompletableFuture.completedFuture(
                                                                                        ResponseEntity.ok(
                                                                                                        new ApiResponse<>(
                                                                                                                        MessageUtil.FAIL,
                                                                                                                        HttpStatus.CONFLICT
                                                                                                                                        .value(),
                                                                                                                        MessageUtil.DUPLICATE_SUBSCRIPTION)));
                                                                }

                                                                // Perform the subscription logic and save the
                                                                // subscription
                                                                // SubscriberProducer newSubscription = new
                                                                // SubscriberProducer();
                                                                // newSubscription.setProducerId(producerId);
                                                                // newSubscription.setSubscriberId(subscriberId);
                                                                // newSubscription.setSubscriber(null);
                                                                // newSubscription.setProducer(null);

                                                                return ((CompletionStage<ResponseEntity<List<String>>>) producerRepository
                                                                                .save(SubscriberProducer.builder()
                                                                                                .producerId(producerId)
                                                                                                .subscriberId(subscriberId)
                                                                                                .build()))
                                                                                .thenApply(savedSubscription -> ResponseEntity
                                                                                                .ok(new ApiResponse<>(
                                                                                                                MessageUtil.SUCCESS,
                                                                                                                HttpStatus.OK.value(),
                                                                                                                MessageUtil.SUBSCRIPTION_SUCCESSFUL)));
                                                        });
                                })
                                .exceptionally(ex -> {
                                        // Log the exception (you can use your logging framework here)
                                        // log.error("Exception occurred while subscribing:", ex);
                                        ex.printStackTrace(); // Temporary console print for debugging

                                        // Return a generic error response
                                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                                        .body(new ApiResponse<>(MessageUtil.FAIL,
                                                                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                                                        "An error occurred while processing your subscription."));
                                });
        }

}
