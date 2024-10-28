package com.kadir.abdul.Twitter_App.service.Imlp;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.kadir.abdul.Twitter_App.dto.AddUserRequest;
import com.kadir.abdul.Twitter_App.dto.UserDto;
import com.kadir.abdul.Twitter_App.entity.User;
import com.kadir.abdul.Twitter_App.repository.UserRepository;
import com.kadir.abdul.Twitter_App.response.ApiResponse;
import com.kadir.abdul.Twitter_App.service.UserService;
import com.kadir.abdul.Twitter_App.utils.MessageUtil;

@Service
public class UserServiceImpl implements UserService {
        @Autowired
        private final UserRepository userRepository;

        private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

        public UserServiceImpl(UserRepository userRepository) {
                this.userRepository = userRepository;
        }

        @Async
        public CompletableFuture<User> validateUserExists(Long userId) {
                return CompletableFuture.supplyAsync(() -> userRepository.findById(userId))
                                .thenApply(optionalUser -> {
                                        if (optionalUser.isPresent()) {
                                                return optionalUser.get();
                                        } else {
                                                throw new RuntimeException("User with ID " + userId + " not found");
                                        }
                                });
        }

        @Async
        @Override
        public CompletableFuture<ResponseEntity<ApiResponse<String>>> addUser(AddUserRequest request) {
                String username = request.getUName();

                logger.info("Users found for role: {}", username);
                return userRepository.findByUsername(username)
                                .thenCompose(optionalUser -> {
                                        if (optionalUser.isPresent()) {
                                                // User already exists, return conflict response
                                                ApiResponse<String> response = new ApiResponse<>(
                                                                MessageUtil.FAIL,
                                                                HttpStatus.CONFLICT.value(),
                                                                MessageUtil.DUPLICATE_USER);
                                                return CompletableFuture.completedFuture(ResponseEntity
                                                                .status(HttpStatus.CONFLICT)
                                                                .body(response));
                                        } else {
                                                // User doesn't exist, proceed to save new user
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
                                                        MessageUtil.USER_ADDED));
                }).exceptionally(ex -> {
                        ApiResponse<String> response = new ApiResponse<>(MessageUtil.INTERNAL_ERROR,
                                        HttpStatus.SERVICE_UNAVAILABLE.value(),
                                        MessageUtil.INTERNAL_ERROR);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(response);
                });
        }

        /**
         * Retrieves a list of users based on their role and maps them to UserResponse
         * objects.
         * Returns a ResponseEntity containing the list of UserResponse objects.
         *
         * @param roleName The role name used to filter users.
         * @return A Future of ResponseEntity containing a list of UserResponse objects.
         */

        @Override
        public CompletableFuture<ResponseEntity<ApiResponse<List<UserDto>>>> findUserListByRole(String role) {

                return CompletableFuture.supplyAsync(() -> userRepository.findUserByRole(role))
                                .thenCompose(users -> {
                                        if (users.isEmpty() || users == null) {
                                                return CompletableFuture.completedFuture(
                                                                ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                                                .body(new ApiResponse<>(
                                                                                                "No users found for the given role")));
                                        }

                                        // Logging the event (assuming logger is properly configured)
                                        logger.info("Users found for role: {}", role);

                                        List<UserDto> userDtos = users.stream()
                                                        .map(user -> new UserDto(user.getUid(), user.getUName(),
                                                                        user.getURole()))
                                                        .collect(Collectors.toList());

                                        ApiResponse<List<UserDto>> response = new ApiResponse<>(
                                                        MessageUtil.SUCCESS,
                                                        HttpStatus.OK.value(),
                                                        userDtos);

                                        return CompletableFuture.completedFuture(
                                                        ResponseEntity.status(HttpStatus.OK).body(response));
                                });

        }

        /**
         * Retrieves a user by their ID and returns it as a Mono<User>.
         * Throws a RecordNotFoundException if no user is found with the given ID.
         *
         * @param id The ID of the user to retrieve.
         * @return A CompletableFuture of User.
         */
        @Override
        public CompletableFuture<ResponseEntity<ApiResponse<UserDto>>> findById(Long id) {
                return CompletableFuture.supplyAsync(() -> userRepository.findById(id))
                                .thenCompose(userOptional -> {
                                        if (userOptional.isEmpty()) {
                                                return CompletableFuture.completedFuture(
                                                                ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                                                .body(new ApiResponse<>(
                                                                                                MessageUtil.RECORD_NOT_FOUND,
                                                                                                HttpStatus.NOT_FOUND
                                                                                                                .value())));
                                        }

                                        // Convert the found User entity to UserDto
                                        User user = userOptional.get();
                                        UserDto userDto = new UserDto(user.getUid(), user.getUName(), user.getURole());

                                        // Wrap the UserDto in ApiResponse and ResponseEntity
                                        return CompletableFuture.completedFuture(
                                                        ResponseEntity.ok(new ApiResponse<>(MessageUtil.SUCCESS,
                                                                        HttpStatus.OK.value(), userDto)));
                                });
        }

}
