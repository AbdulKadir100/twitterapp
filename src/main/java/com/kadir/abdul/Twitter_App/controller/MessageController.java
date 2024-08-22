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

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/message")
public class MessageController {
    @Autowired
    private MessageService messageService;


    

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }




    @PostMapping("/publish")
    public CompletableFuture<ResponseEntity<ApiResponse<String>>> publishMessage(
            @RequestBody PublishMesssageRequest request) {
        return messageService.publishMessage(request);
    }

}
