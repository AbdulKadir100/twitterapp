package com.kadir.abdul.Twitter_App.controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kadir.abdul.Twitter_App.dto.AddUserRequest;
import com.kadir.abdul.Twitter_App.dto.UserDto;
import com.kadir.abdul.Twitter_App.response.ApiResponse;
import com.kadir.abdul.Twitter_App.service.UserService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Add a new user.
     * 
     * @param request AddUserRequest containing user details.
     * @return CompletableFuture with ResponseEntity containing ApiResponse.
     */
    @PostMapping("/addUser")
    public CompletableFuture<ResponseEntity<ApiResponse<String>>> addUser(@RequestBody AddUserRequest request) {
        return userService.addUser(request);
    }

    /**
     * Find users by role.
     * 
     * @param role Role to filter users.
     * @return CompletableFuture with ResponseEntity containing ApiResponse.
     */
    @GetMapping("/role")
    public CompletableFuture<ResponseEntity<ApiResponse<List<UserDto>>>> findByUserRole(
            @RequestParam(name = "role") String role) {
        return userService.findUserListByRole(role).exceptionally(ex -> {
            ApiResponse<List<UserDto>> response = new ApiResponse<>(
                    ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        });
    }

    /**
     * Get user by ID.
     * 
     * @param id User ID.
     * @return CompletableFuture with ResponseEntity containing ApiResponse.
     */
    @GetMapping("/users/{id}")
    public CompletableFuture<ResponseEntity<ApiResponse<UserDto>>> getUserById(@PathVariable Long id) {
        return userService.findById(id).exceptionally(ex -> {
            ApiResponse<UserDto> response = new ApiResponse<>(
                    ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        });
    }
}
