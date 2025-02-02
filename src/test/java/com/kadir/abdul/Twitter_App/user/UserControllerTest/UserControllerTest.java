package com.kadir.abdul.Twitter_App.user.UserControllerTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
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

import com.kadir.abdul.Twitter_App.controller.FetchAllUserSubscribersController;
import com.kadir.abdul.Twitter_App.dto.MessageResponse;
import com.kadir.abdul.Twitter_App.dto.UserResponse;
import com.kadir.abdul.Twitter_App.response.ApiResponse;
import com.kadir.abdul.Twitter_App.service.MessageService;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private MessageService messageService;

    @InjectMocks
    private FetchAllUserSubscribersController fetchAllUserSubscribersController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
public void testGetMessageBySubscriberId() throws Exception {
    // Arrange
    Long subscriberId = 1L;
    UserResponse userResponse = new UserResponse(101L, "john_doe");
    MessageResponse messageResponse = new MessageResponse("Hello, World!", 201L, userResponse);
    ApiResponse<List<MessageResponse>> apiResponse = new ApiResponse<>("Success", HttpStatus.OK.value(), Collections.singletonList(messageResponse));

    CompletableFuture<ResponseEntity<ApiResponse<List<MessageResponse>>>> futureResponse =
        CompletableFuture.completedFuture(ResponseEntity.ok(apiResponse));

    // Mock the service method
    when(messageService.getMessageBySubscriberId(subscriberId)).thenReturn(futureResponse);

    // Act
    CompletableFuture<ResponseEntity<ApiResponse<List<MessageResponse>>>> result =
        fetchAllUserSubscribersController.getMessageBySubscriberId(subscriberId);

    // Assert
    assertNotNull(result, "The result should not be null");

    // Wait for the CompletableFuture to complete and get the response
    ResponseEntity<ApiResponse<List<MessageResponse>>> responseEntity = result.get();

    // Verify the response status code
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "The status code should be OK");

    // Verify the response body
    ApiResponse<List<MessageResponse>> responseBody = responseEntity.getBody();
    assertNotNull(responseBody, "The response body should not be null");
    assertEquals("Success", responseBody.getResponseMessage(), "The response message should be 'Success'");

    // Verify the contents of the MessageResponse
    List<MessageResponse> messages = responseBody.getData();
    assertNotNull(messages, "The messages list should not be null");
    assertEquals(1, messages.size(), "There should be exactly one message in the response");

    MessageResponse actualMessageResponse = messages.get(0);
    assertEquals("Hello, World!", actualMessageResponse.getContents(), "The message content should match");
    assertEquals(201L, actualMessageResponse.getMid(), "The message ID should match");
    assertEquals(101L, actualMessageResponse.getPostedBy().getUid(), "The user ID should match");
    assertEquals("john_doe", actualMessageResponse.getPostedBy().getUName(), "The username should match");

    // Verify that the service method was called
    verify(messageService, times(1)).getMessageBySubscriberId(subscriberId);
}
    
}
