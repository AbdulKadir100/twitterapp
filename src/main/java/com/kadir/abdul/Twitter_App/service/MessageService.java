package com.kadir.abdul.Twitter_App.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;

import com.kadir.abdul.Twitter_App.dto.MessageResponse;
import com.kadir.abdul.Twitter_App.dto.PublishMesssageRequest;
import com.kadir.abdul.Twitter_App.response.ApiResponse;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

public interface MessageService {

    CompletableFuture<ResponseEntity<ApiResponse<List<MessageResponse>>>> getUserMessages(Long userId);

    CompletableFuture<ResponseEntity<ApiResponse<List<MessageResponse>>>> getMessageBySubscriberId(Long subscriberId);

    CompletableFuture<ResponseEntity<ApiResponse<String>>> publishMessage(@RequestBody PublishMesssageRequest request);

}
