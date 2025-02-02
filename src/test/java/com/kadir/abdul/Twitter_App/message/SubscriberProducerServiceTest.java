package com.kadir.abdul.Twitter_App.message;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CompletableFuture;

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
import com.kadir.abdul.Twitter_App.service.Imlp.SubscriberProducerServiceImpl;
import com.kadir.abdul.Twitter_App.service.UserService;
import com.kadir.abdul.Twitter_App.utils.MessageUtil;

public class SubscriberProducerServiceTest {

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
    public void testSubscribe_Success() throws Exception {
        // Arrange
        Long subscriberId = 1L;
        Long producerId = 2L;
        Subscribe request = new Subscribe(subscriberId, producerId);
    
        // Mock UserDto
        UserDto subscriberDto = UserDto.builder().uid(subscriberId).uName("Subscriber").build();
        UserDto producerDto = UserDto.builder().uid(producerId).uName("ProducerUser").uRole("Producer").build();
    
        when(userService.findById(subscriberId)).thenReturn(CompletableFuture.completedFuture(
                ResponseEntity.ok(new ApiResponse<>(MessageUtil.SUCCESS, HttpStatus.OK.value(), subscriberDto))));
    
        when(userService.findById(producerId)).thenReturn(CompletableFuture.completedFuture(
                ResponseEntity.ok(new ApiResponse<>(MessageUtil.SUCCESS, HttpStatus.OK.value(), producerDto))));
    
        when(producerRepository.checkSubscriptionStatus(subscriberId, producerId))
                .thenReturn(CompletableFuture.completedFuture(null)); // ✅ Only once
    
        CompletableFuture<ResponseEntity<ApiResponse<String>>> futureResponse =
                subscriberProducerService.subscribe(request);
        ResponseEntity<ApiResponse<String>> response = futureResponse.get(); // Wait for completion
    
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    
        ApiResponse<String> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(MessageUtil.SUCCESS, responseBody.getResponseMessage());
        assertEquals(HttpStatus.OK.value(), responseBody.getResponseCode());
        assertEquals(MessageUtil.SUBSCRIPTION_SUCCESSFUL, responseBody.getData());
    
        // Verify Interactions
        verify(userService, times(1)).findById(subscriberId);
        verify(userService, times(1)).findById(producerId);
        verify(producerRepository, times(1)).checkSubscriptionStatus(subscriberId, producerId);
        verify(producerRepository, times(1)).save(any(SubscriberProducer.class));  // ✅ Now correctly mocked
    }
    

    @Test
    public void testSubscribe_SubscriberNotFound() throws Exception {
        // Arrange
        Long subscriberId = 1L;
        Long producerId = 2L;
        Subscribe request = new Subscribe(subscriberId, producerId);

        when(userService.findById(subscriberId)).thenReturn(CompletableFuture.completedFuture(
                ResponseEntity.ok(new ApiResponse<>(MessageUtil.FAIL, HttpStatus.NOT_FOUND.value(), null))));

        // Act
        CompletableFuture<ResponseEntity<ApiResponse<String>>> futureResponse = subscriberProducerService
                .subscribe(request);
        ResponseEntity<ApiResponse<String>> response = futureResponse.get();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getResponseCode());
        assertEquals(MessageUtil.RECORD_NOT_FOUND, response.getBody().getData());

        // Verify Interactions
        verify(userService, times(1)).findById(subscriberId);
        verify(userService, never()).findById(producerId);
        verify(producerRepository, never()).checkSubscriptionStatus(anyLong(), anyLong());
    }

    @Test
    public void testSubscribe_ProducerRoleInvalid() throws Exception {
        // Arrange
        Long subscriberId = 1L;
        Long producerId = 2L;
        Subscribe request = new Subscribe(subscriberId, producerId);

        UserDto subscriberDto = UserDto.builder().uid(subscriberId).uName("Subscriber").build();
        UserDto producerDto = UserDto.builder().uid(producerId).uName("User").uRole("Subscriber").build();

        when(userService.findById(subscriberId)).thenReturn(CompletableFuture.completedFuture(
                ResponseEntity.ok(new ApiResponse<>(MessageUtil.SUCCESS, HttpStatus.OK.value(), subscriberDto))));

        when(userService.findById(producerId)).thenReturn(CompletableFuture.completedFuture(
                ResponseEntity.ok(new ApiResponse<>(MessageUtil.SUCCESS, HttpStatus.OK.value(), producerDto))));

        // Act
        CompletableFuture<ResponseEntity<ApiResponse<String>>> futureResponse = subscriberProducerService
                .subscribe(request);
        ResponseEntity<ApiResponse<String>> response = futureResponse.get();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN.value(), response.getBody().getResponseCode());
        assertEquals(MessageUtil.SUBSCRIPTION_NOT_ALLOW, response.getBody().getData());

        // Verify Interactions
        verify(userService, times(1)).findById(subscriberId);
        verify(userService, times(1)).findById(producerId);
        verify(producerRepository, never()).checkSubscriptionStatus(anyLong(), anyLong());
    }

    @Test
    public void testSubscribe_DuplicateSubscription() throws Exception {
        // Arrange
        Long subscriberId = 1L;
        Long producerId = 2L;
        Subscribe request = new Subscribe(subscriberId, producerId);

        UserDto subscriberDto = UserDto.builder().uid(subscriberId).uName("Subscriber").build();
        UserDto producerDto = UserDto.builder().uid(producerId).uName("ProducerUser").uRole("Producer").build();

        when(userService.findById(subscriberId)).thenReturn(CompletableFuture.completedFuture(
                ResponseEntity.ok(new ApiResponse<>(MessageUtil.SUCCESS, HttpStatus.OK.value(), subscriberDto))));

        when(userService.findById(producerId)).thenReturn(CompletableFuture.completedFuture(
                ResponseEntity.ok(new ApiResponse<>(MessageUtil.SUCCESS, HttpStatus.OK.value(), producerDto))));

        // when(producerRepository.checkSubscriptionStatus(subscriberId, producerId))
        // .thenReturn(CompletableFuture.completedFuture(new SubscriberProducer()));

        // Act
        CompletableFuture<ResponseEntity<ApiResponse<String>>> futureResponse = subscriberProducerService
                .subscribe(request);
        ResponseEntity<ApiResponse<String>> response = futureResponse.get();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT.value(), response.getBody().getResponseCode());
        assertEquals(MessageUtil.DUPLICATE_SUBSCRIPTION, response.getBody().getData());

        // Verify Interactions
        verify(producerRepository, never()).save(any(SubscriberProducer.class));
    }
}
