package com.kadir.abdul.Twitter_App.message;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.kadir.abdul.Twitter_App.dto.Subscribe;
import com.kadir.abdul.Twitter_App.dto.UserDto;
import com.kadir.abdul.Twitter_App.entity.SubscriberProducer;
import com.kadir.abdul.Twitter_App.repository.SubscriberProducerRepository;
import com.kadir.abdul.Twitter_App.response.ApiResponse;
import com.kadir.abdul.Twitter_App.service.UserService;
import com.kadir.abdul.Twitter_App.service.Imlp.SubscriberProducerServiceImpl;
import com.kadir.abdul.Twitter_App.utils.MessageUtil;

public class SubscriberProducerServiceImplTest {

    @Mock
    private SubscriberProducerRepository producerRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private SubscriberProducerServiceImpl subscriberProducerService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testListProducerBySubscriber() throws InterruptedException, ExecutionException {
        // Arrange
        Long subscriberId = 1L;
        List<Long> producerIds = Arrays.asList(2L, 3L);
        when(producerRepository.listProducerBySubscriber(subscriberId))
                .thenReturn(CompletableFuture.completedFuture(producerIds));

        // Act
        CompletableFuture<ResponseEntity<List<Long>>> resultFuture = subscriberProducerService.listProducerBySubscriber(subscriberId);
        ResponseEntity<List<Long>> result = resultFuture.get();

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(producerIds, result.getBody());
        verify(producerRepository, times(1)).listProducerBySubscriber(subscriberId);
    }

   
     @Test
public void testSubscribe_Success() throws InterruptedException, ExecutionException {
    // Arrange
    Subscribe request = new Subscribe();
    request.setSubscriberID(1L);
    request.setUserId(2L);

    UserDto subscriber = new UserDto();
    subscriber.setUid(1L);
    UserDto producer = new UserDto();
    producer.setUid(2L);
    producer.setURole("Producer");

    ApiResponse<UserDto> subscriberResponse = new ApiResponse<>(MessageUtil.SUCCESS, HttpStatus.OK.value(), subscriber);
    ApiResponse<UserDto> producerResponse = new ApiResponse<>(MessageUtil.SUCCESS, HttpStatus.OK.value(), producer);

    // Mock userService.findById calls
    when(userService.findById(1L)).thenReturn(CompletableFuture.completedFuture(ResponseEntity.ok(subscriberResponse)));
    when(userService.findById(2L)).thenReturn(CompletableFuture.completedFuture(ResponseEntity.ok(producerResponse)));

    // Mock producerRepository.checkSubscriptionStatus
    when(producerRepository.checkSubscriptionStatus(1L, 2L)).thenReturn(CompletableFuture.completedFuture(null));

    // Mock producerRepository.save
    SubscriberProducer savedSubscription = SubscriberProducer.builder()
            .producerId(2L)
            .subscriberId(1L)
            .build();
    when(producerRepository.save(any(SubscriberProducer.class)))
        .thenReturn(CompletableFuture.completedFuture(savedSubscription));

    // Act
    CompletableFuture<ResponseEntity<ApiResponse<String>>> resultFuture = subscriberProducerService.subscribe(request);
    ResponseEntity<ApiResponse<String>> result = resultFuture.get();

    // Assert
    assertEquals(HttpStatus.OK, result.getStatusCode());
    assertEquals(MessageUtil.SUCCESS, result.getBody().getResponseMessage());
    assertEquals(MessageUtil.SUBSCRIPTION_SUCCESSFUL, result.getBody().getData());
    verify(userService, times(1)).findById(1L);
    verify(userService, times(1)).findById(2L);
    verify(producerRepository, times(1)).checkSubscriptionStatus(1L, 2L);
    verify(producerRepository, times(1)).save(any(SubscriberProducer.class));
}

    @Test
    public void testSubscribe_SubscriberNotFound() throws InterruptedException, ExecutionException {
        // Arrange
        Subscribe request = new Subscribe();
        request.setSubscriberID(1L);
        request.setUserId(2L);

        ApiResponse<UserDto> subscriberResponse = new ApiResponse<>(MessageUtil.FAIL, HttpStatus.NOT_FOUND.value(), null);
        ApiResponse<UserDto> producerResponse = new ApiResponse<>(MessageUtil.SUCCESS, HttpStatus.OK.value(), new UserDto());

        when(userService.findById(1L)).thenReturn(CompletableFuture.completedFuture(ResponseEntity.ok(subscriberResponse)));
        when(userService.findById(2L)).thenReturn(CompletableFuture.completedFuture(ResponseEntity.ok(producerResponse)));

        // Act
        CompletableFuture<ResponseEntity<ApiResponse<String>>> resultFuture = subscriberProducerService.subscribe(request);
        ResponseEntity<ApiResponse<String>> result = resultFuture.get();

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(MessageUtil.FAIL, result.getBody().getResponseMessage());
        assertEquals(MessageUtil.RECORD_NOT_FOUND, result.getBody().getData());
        verify(userService, times(1)).findById(1L);
        verify(userService, times(1)).findById(2L);
        verify(producerRepository, never()).checkSubscriptionStatus(anyLong(), anyLong());
        verify(producerRepository, never()).save(any(SubscriberProducer.class));
    }

    @Test
    public void testSubscribe_ProducerNotAProducer() throws InterruptedException, ExecutionException {
        // Arrange
        Subscribe request = new Subscribe();
        request.setSubscriberID(1L);
        request.setUserId(2L);

        UserDto subscriber = new UserDto();
        subscriber.setUid(1L);
        UserDto producer = new UserDto();
        producer.setUid(2L);
        producer.setURole("Consumer");

        ApiResponse<UserDto> subscriberResponse = new ApiResponse<>(MessageUtil.SUCCESS, HttpStatus.OK.value(), subscriber);
        ApiResponse<UserDto> producerResponse = new ApiResponse<>(MessageUtil.SUCCESS, HttpStatus.OK.value(), producer);

        when(userService.findById(1L)).thenReturn(CompletableFuture.completedFuture(ResponseEntity.ok(subscriberResponse)));
        when(userService.findById(2L)).thenReturn(CompletableFuture.completedFuture(ResponseEntity.ok(producerResponse)));

        // Act
        CompletableFuture<ResponseEntity<ApiResponse<String>>> resultFuture = subscriberProducerService.subscribe(request);
        ResponseEntity<ApiResponse<String>> result = resultFuture.get();

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(MessageUtil.FAIL, result.getBody().getResponseMessage());
        assertEquals(MessageUtil.SUBSCRIPTION_NOT_ALLOW, result.getBody().getData());
        verify(userService, times(1)).findById(1L);
        verify(userService, times(1)).findById(2L);
        verify(producerRepository, never()).checkSubscriptionStatus(anyLong(), anyLong());
        verify(producerRepository, never()).save(any(SubscriberProducer.class));
    }

    @Test
    public void testSubscribe_DuplicateSubscription() throws InterruptedException, ExecutionException {
        // Arrange
        Subscribe request = new Subscribe();
        request.setSubscriberID(1L);
        request.setUserId(2L);

        UserDto subscriber = new UserDto();
        subscriber.setUid(1L);
        UserDto producer = new UserDto();
        producer.setUid(2L);
        producer.setURole("Producer");

        ApiResponse<UserDto> subscriberResponse = new ApiResponse<>(MessageUtil.SUCCESS, HttpStatus.OK.value(), subscriber);
        ApiResponse<UserDto> producerResponse = new ApiResponse<>(MessageUtil.SUCCESS, HttpStatus.OK.value(), producer);

        when(userService.findById(1L)).thenReturn(CompletableFuture.completedFuture(ResponseEntity.ok(subscriberResponse)));
        when(userService.findById(2L)).thenReturn(CompletableFuture.completedFuture(ResponseEntity.ok(producerResponse)));
        when(producerRepository.checkSubscriptionStatus(1L, 2L)).thenReturn(CompletableFuture.completedFuture(new SubscriberProducer()));

        // Act
        CompletableFuture<ResponseEntity<ApiResponse<String>>> resultFuture = subscriberProducerService.subscribe(request);
        ResponseEntity<ApiResponse<String>> result = resultFuture.get();

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(MessageUtil.FAIL, result.getBody().getResponseMessage());
        assertEquals(MessageUtil.DUPLICATE_SUBSCRIPTION, result.getBody().getData());
        verify(userService, times(1)).findById(1L);
        verify(userService, times(1)).findById(2L);
        verify(producerRepository, times(1)).checkSubscriptionStatus(1L, 2L);
        verify(producerRepository, never()).save(any(SubscriberProducer.class));
    }
}