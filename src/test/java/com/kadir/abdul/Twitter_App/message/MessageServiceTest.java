package com.kadir.abdul.Twitter_App.message;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.kadir.abdul.Twitter_App.dto.MessageResponse;
import com.kadir.abdul.Twitter_App.dto.PublishMesssageRequest;
import com.kadir.abdul.Twitter_App.dto.UserResponse;
import com.kadir.abdul.Twitter_App.entity.Message;
import com.kadir.abdul.Twitter_App.entity.User;
import com.kadir.abdul.Twitter_App.repository.MessageRepository;
import com.kadir.abdul.Twitter_App.repository.SubscriberProducerRepository;
import com.kadir.abdul.Twitter_App.repository.UserRepository;
import com.kadir.abdul.Twitter_App.response.ApiResponse;
import com.kadir.abdul.Twitter_App.service.Imlp.MessageServiceImpl;
import com.kadir.abdul.Twitter_App.utils.MessageUtil;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SubscriberProducerRepository producerRepository;

    @InjectMocks
    private MessageServiceImpl messageService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetMessageBySubscriberId() throws Exception {
        // Arrange
        Long subscriberId = 1L;
        Long producerId = 2L;
        Long messageId = 101L;

        // Mock user
        User user = new User();
        user.setUid(producerId);
        user.setUName("producer_user");

        // Mock message
        Message message = new Message();
        message.setMid(messageId);
        message.setContents("Hello, Subscriber!");
        message.setUid(producerId);
        message.setUser(user);

        // Mock user response
        UserResponse userResponse = UserResponse.builder()
                .uid(producerId)
                .uName("producer_user")
                .build();

        // Mock message response
        MessageResponse messageResponse = MessageResponse.builder()
                .contents("Hello, Subscriber!")
                .mid(messageId)
                .postedBy(userResponse)
                .build();

        // Mock repository responses
        when(userRepository.findById(subscriberId)).thenReturn(Optional.of(user));
        when(producerRepository.listProducerBySubscriber(subscriberId))
                .thenReturn(CompletableFuture.completedFuture(Arrays.asList(producerId)));
        when(messageRepository.listMessageByProducerIdIn(Arrays.asList(producerId)))
                .thenReturn(CompletableFuture.completedFuture(Arrays.asList(message)));
        when(userRepository.findById(producerId)).thenReturn(Optional.of(user));

        // Act
        CompletableFuture<ResponseEntity<ApiResponse<List<MessageResponse>>>> resultFuture = messageService
                .getMessageBySubscriberId(subscriberId);

        // Wait for the CompletableFuture to complete
        ResponseEntity<ApiResponse<List<MessageResponse>>> result = resultFuture.get();

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());

        ApiResponse<List<MessageResponse>> responseBody = result.getBody();
        assertNotNull(responseBody);
        assertEquals(MessageUtil.SUCCESS, responseBody.getResponseMessage());
        assertEquals(HttpStatus.OK.value(), responseBody.getResponseCode());

        List<MessageResponse> messages = responseBody.getData();
        assertNotNull(messages);
        assertEquals(1, messages.size());

        MessageResponse actualMessageResponse = messages.get(0);
        assertEquals("Hello, Subscriber!", actualMessageResponse.getContents());
        assertEquals(messageId, actualMessageResponse.getMid());
        assertEquals(producerId, actualMessageResponse.getPostedBy().getUid());
        assertEquals("producer_user", actualMessageResponse.getPostedBy().getUName());

        // Verify interactions
        // verify(userRepository, times(1)).findById(subscriberId);
        verify(producerRepository, times(1)).listProducerBySubscriber(subscriberId);
        verify(messageRepository, times(1)).listMessageByProducerIdIn(Arrays.asList(producerId));
        verify(userRepository, times(1)).findById(producerId);
    }

    @Test
    public void testGetUserMessages_Success() throws Exception {
        // Arrange
        Long userId = 1L;
        Long messageId = 101L;

        User user = new User();
        user.setUid(userId);
        user.setUName("test_user");

        Message message = new Message();
        message.setMid(messageId);
        message.setContents("Hello, World!");
        message.setUid(userId);
        message.setUser(user);

        UserResponse userResponse = UserResponse.builder()
                .uid(userId)
                .uName("test_user")
                .build();

        MessageResponse messageResponse = MessageResponse.builder()
                .contents("Hello, World!")
                .mid(messageId)
                .postedBy(userResponse)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(messageRepository.listMessageByProducerId(userId))
                .thenReturn(CompletableFuture.completedFuture(List.of(message)));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        CompletableFuture<ResponseEntity<ApiResponse<List<MessageResponse>>>> resultFuture = messageService
                .getUserMessages(userId);

        ResponseEntity<ApiResponse<List<MessageResponse>>> result = resultFuture.get();

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());

        ApiResponse<List<MessageResponse>> responseBody = result.getBody();
        assertNotNull(responseBody);
        assertEquals(MessageUtil.SUCCESS, responseBody.getResponseMessage());
        assertEquals(HttpStatus.OK.value(), responseBody.getResponseCode());

        List<MessageResponse> messages = responseBody.getData();
        assertNotNull(messages);
        assertEquals(1, messages.size());

        MessageResponse actualMessageResponse = messages.get(0);
        assertEquals("Hello, World!", actualMessageResponse.getContents());
        assertEquals(messageId, actualMessageResponse.getMid());
        assertEquals(userId, actualMessageResponse.getPostedBy().getUid());
        assertEquals("test_user", actualMessageResponse.getPostedBy().getUName());

        // Verify interactions
        // verify(userRepository, times(1)).findById(userId);
        verify(messageRepository, times(1)).listMessageByProducerId(userId);
    }

    @Test
    public void testGetUserMessages_NoMessages() throws Exception {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setUid(userId);
        user.setUName("test_user");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(messageRepository.listMessageByProducerId(userId))
                .thenReturn(CompletableFuture.completedFuture(Collections.emptyList()));

        // Act
        CompletableFuture<ResponseEntity<ApiResponse<List<MessageResponse>>>> resultFuture = messageService
                .getUserMessages(userId);

        ResponseEntity<ApiResponse<List<MessageResponse>>> result = resultFuture.get();

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());

        ApiResponse<List<MessageResponse>> responseBody = result.getBody();
        assertNotNull(responseBody);
        assertEquals(MessageUtil.SUCCESS, responseBody.getResponseMessage());
        assertEquals(HttpStatus.OK.value(), responseBody.getResponseCode());
        assertNotNull(responseBody.getData());
        assertTrue(responseBody.getData().isEmpty());

        // Verify interactions
        // verify(userRepository, times(1)).findById(userId);
        verify(messageRepository, times(1)).listMessageByProducerId(userId);
    }

    @Test
    public void testHandleFailure_ReturnsInternalServerError() {
        // Arrange
        Throwable exception = new RuntimeException("Something went wrong");

        // Act
        ResponseEntity<ApiResponse<List<MessageResponse>>> response = messageService.handleFailure(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        ApiResponse<List<MessageResponse>> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(HttpStatus.EXPECTATION_FAILED.toString(), responseBody.getResponseMessage());
        assertEquals(HttpStatus.EXPECTATION_FAILED.value(), responseBody.getResponseCode());
        assertNull(responseBody.getData());
    }

    @Test
    public void testPublishMessage_Success() throws Exception {
        // Arrange
        Long userId = 1L;
        String messageContent = "Hello, World!";
        PublishMesssageRequest request = new PublishMesssageRequest(userId, messageContent);

        // Mock User
        User user = new User();
        user.setUid(userId);
        user.setUName("TestUser");

        // Mock Message
        Message savedMessage = new Message();
        savedMessage.setUid(userId);
        savedMessage.setContents(messageContent);
        savedMessage.setUser(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(messageRepository.save(any(Message.class))).thenReturn(savedMessage);

        // Act
        CompletableFuture<ResponseEntity<ApiResponse<String>>> futureResponse = messageService.publishMessage(request);
        ResponseEntity<ApiResponse<String>> response = futureResponse.get();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ApiResponse<String> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(MessageUtil.SUCCESS, responseBody.getResponseMessage());
        assertEquals(HttpStatus.OK.value(), responseBody.getResponseCode());
        assertEquals("Message published successfully", responseBody.getData());

        // Verify Interactions
        //verify(userRepository, times(1)).findById(userId);
        verify(messageRepository, times(1)).save(any(Message.class));
    }


    @Test
    public void testPublishMessage_MessageSaveFails() throws Exception {
        // Arrange
        Long userId = 1L;
        String messageContent = "Hello, World!";
        PublishMesssageRequest request = new PublishMesssageRequest(userId, messageContent);

        // Mock User
        User user = new User();
        user.setUid(userId);
        user.setUName("TestUser");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(messageRepository.save(any(Message.class))).thenThrow(new RuntimeException("Database error"));

        // Act
        CompletableFuture<ResponseEntity<ApiResponse<String>>> futureResponse = messageService.publishMessage(request);
        ResponseEntity<ApiResponse<String>> response = futureResponse.get();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        ApiResponse<String> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(MessageUtil.FAIL, responseBody.getResponseMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), responseBody.getResponseCode());
        assertEquals("Message not published", responseBody.getData());

        // Verify Interactions
        //verify(userRepository, times(1)).findById(userId);
        verify(messageRepository, times(1)).save(any(Message.class));
    }

}