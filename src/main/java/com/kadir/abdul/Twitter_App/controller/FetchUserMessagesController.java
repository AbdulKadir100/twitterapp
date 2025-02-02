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
@RequestMapping("/api/v1/user")
@Tag(description = "Fetch User message", name = "MESSAGE")
public class FetchUserMessagesController {

    @Autowired
    private final MessageService messageService;

    
    public FetchUserMessagesController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * Get all messages for a specific user.
     * 
     * @param id User ID.
     * @return CompletableFuture with ResponseEntity containing ApiResponse with
     *         list of messages.
     */
    @GetMapping("/{id}")
    @Operation(description = "Get all user messages")
    public CompletableFuture<ResponseEntity<ApiResponse<List<MessageResponse>>>> getUserMessages(
            @PathVariable(name = "id") Long id) {
        return messageService.getUserMessages(id);
    }
}
