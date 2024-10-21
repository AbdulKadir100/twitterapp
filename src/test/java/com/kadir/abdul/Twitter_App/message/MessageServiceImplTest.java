package com.kadir.abdul.Twitter_App.message;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.kadir.abdul.Twitter_App.dto.PublishMesssageRequest;
import com.kadir.abdul.Twitter_App.entity.Message;
import com.kadir.abdul.Twitter_App.entity.User;
import com.kadir.abdul.Twitter_App.repository.MessageRepository;
import com.kadir.abdul.Twitter_App.response.ApiResponse;
import com.kadir.abdul.Twitter_App.service.Imlp.MessageServiceImpl;
import com.kadir.abdul.Twitter_App.service.Imlp.UserServiceImpl;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.when;

import java.util.concurrent.CompletableFuture;

public class MessageServiceImplTest {

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private MessageServiceImpl userServiceImpl;

    private User user;
    private PublishMesssageRequest publishMesssageRequest;

     @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Set up user and request objects
        user = new User();
        user.setUid(1L);
        user.setUName("abdul");

        publishMesssageRequest = new PublishMesssageRequest();
        publishMesssageRequest.setUserId(1L);
        publishMesssageRequest.setMessage("Test message");
        
    }

    @Test
    void publishMessage_Success()throws Exception{
        given(userServiceImpl.validateUserExists(1L)).willReturn(CompletableFuture.completedFuture(user));

        // Mocking the messageRepository.save method
        Message message = Message.builder()
                .uid(1L)
                .contents("Test message")
                .user(user)
                .build();

        
        given(messageRepository.save(any(Message.class))).willReturn(message); 
        
        
         // Act
        CompletableFuture<ResponseEntity<ApiResponse<String>>> responseFuture = userServiceImpl.publishMessage(publishMesssageRequest);

        // Get the actual response
        ResponseEntity<ApiResponse<String>> response = responseFuture.join(); // Use get() for testing CompletableFuture results

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Message published successfully", response.getBody().getResponseMessage());
   
    }

}
