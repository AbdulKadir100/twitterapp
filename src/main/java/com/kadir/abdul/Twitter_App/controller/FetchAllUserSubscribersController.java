package com.kadir.abdul.Twitter_App.controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kadir.abdul.Twitter_App.dto.MessageResponse;
import com.kadir.abdul.Twitter_App.response.ApiResponse;
import com.kadir.abdul.Twitter_App.service.MessageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/subscriber")
@Tag(description = "Fetch all User message by Subscriber", name = "SUBSCRIBER")
public class FetchAllUserSubscribersController {
    @Autowired
    private MessageService messageService;

    public FetchAllUserSubscribersController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/{id}")
    @Operation(description = "Get all message by subscriber's id")
    public CompletableFuture<ResponseEntity<ApiResponse<List<MessageResponse>>>> getMessageBySubscriberId(@PathVariable(name = "id") Long id){
        return messageService.getMessageBySubscriberId(id);
        
    }


}
