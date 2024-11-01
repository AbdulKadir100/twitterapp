package com.kadir.abdul.Twitter_App.controller;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kadir.abdul.Twitter_App.dto.PublishMesssageRequest;
import com.kadir.abdul.Twitter_App.response.ApiResponse;
import com.kadir.abdul.Twitter_App.service.MessageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/message")
@Tag(description = "Publish Message", name = "PUBLISH MESSAGE")
public class MessageController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * Publishes user messages.
     * 
     * @param request PublishMessageRequest containing message details.
     * @return CompletableFuture with ResponseEntity containing ApiResponse.
     */
    @Operation(description = "Publishes users messages")
    @PostMapping("/publish")
    public CompletableFuture<ResponseEntity<ApiResponse<String>>> publishMessage(
            @Valid @RequestBody PublishMesssageRequest request) {
        return messageService.publishMessage(request);
    }
}
