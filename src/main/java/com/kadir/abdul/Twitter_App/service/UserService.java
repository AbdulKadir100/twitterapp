package com.kadir.abdul.Twitter_App.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;

import com.kadir.abdul.Twitter_App.dto.AddUserRequest;
import com.kadir.abdul.Twitter_App.dto.UserDto;
import com.kadir.abdul.Twitter_App.response.ApiResponse;

public interface UserService {
    CompletableFuture<ResponseEntity<ApiResponse<String>>> addUser(AddUserRequest request);
    CompletableFuture<ResponseEntity<ApiResponse<List<UserDto>>>> findUserListByRole(String role);

}
