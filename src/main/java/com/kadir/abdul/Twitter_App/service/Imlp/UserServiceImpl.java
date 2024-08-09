package com.kadir.abdul.Twitter_App.service.Imlp;

import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.kadir.abdul.Twitter_App.dto.AddUserRequest;
import com.kadir.abdul.Twitter_App.entity.User;
import com.kadir.abdul.Twitter_App.repository.UserRepository;
import com.kadir.abdul.Twitter_App.response.ApiResponse;
import com.kadir.abdul.Twitter_App.service.UserService;
import com.kadir.abdul.Twitter_App.utils.MessageUtil;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Async
    @Override
    public CompletableFuture<ResponseEntity<ApiResponse<String>>> addUser(AddUserRequest request) {
        String username = request.getUName();

        return userRepository.findByUsername(username)
                .thenCompose(existingUser -> {
                    if (existingUser != null) {
                        ApiResponse<String> response = new ApiResponse<>(MessageUtil.FAIL,
                                HttpStatus.CONFLICT.value(),
                                MessageUtil.DUPLICATE_USER);
                        return CompletableFuture
                                .completedFuture(ResponseEntity.status(HttpStatus.CONFLICT).body(response));
                    } else {
                        return saveNewUser(request);
                    }
                });
    }

   @Async
    public CompletableFuture<ResponseEntity<ApiResponse<String>>> saveNewUser(AddUserRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            User user = User.builder()
                    .uRole(request.getURole())
                    .uName(request.getUName())
                    .build();
            userRepository.save(user);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(
                        MessageUtil.SUCCESS,
                        HttpStatus.CREATED.value(),
                        MessageUtil.USER_ADDED
                    ));
        }).exceptionally(ex -> {
            ApiResponse<String> response = new ApiResponse<>(MessageUtil.INTERNAL_ERROR,
                                HttpStatus.SERVICE_UNAVAILABLE.value(),
                                MessageUtil.INTERNAL_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        });
    }
}
