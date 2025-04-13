package com.kadir.abdul.Twitter_App.message;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.kadir.abdul.Twitter_App.dto.AddUserRequest;
import com.kadir.abdul.Twitter_App.dto.UserDto;
import com.kadir.abdul.Twitter_App.entity.User;
import com.kadir.abdul.Twitter_App.repository.UserRepository;
import com.kadir.abdul.Twitter_App.response.ApiResponse;
import com.kadir.abdul.Twitter_App.service.Imlp.UserServiceImpl;

@SpringBootTest
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testValidateUserExists_UserExists() throws ExecutionException, InterruptedException {
        // Given
        User user = new User();
        user.setUid(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        CompletableFuture<User> result = userService.validateUserExists(1L);

        // Then
        assertDoesNotThrow(() -> result.get());
        assertEquals(user, result.get());
    }
    @Test
    void testValidateUserExists_UserNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        CompletableFuture<User> result = userService.validateUserExists(1L);

        // Then
        assertThrows(RuntimeException.class, () -> result.join());
    }

    @Test
    void testAddUser_UserAlreadyExists() {
        // Given
        AddUserRequest request = new AddUserRequest();
        request.setUName("testUser");

        User user = new User();
        user.setUid(1L);
        when(userRepository.findByUsername("testUser")).thenReturn(CompletableFuture.completedFuture(Optional.of(user)));

        // When
        CompletableFuture<ResponseEntity<ApiResponse<String>>> result = userService.addUser(request);

        // Then
        assertEquals(HttpStatus.CONFLICT, result.join().getStatusCode());
    }

    @Test
    void testAddUser_NewUser() {
        // Given
        AddUserRequest request = new AddUserRequest();
        request.setUName("newUser");
        request.setURole("ROLE_USER");

        when(userRepository.findByUsername("newUser")).thenReturn(CompletableFuture.completedFuture(Optional.empty()));

        // When
        CompletableFuture<ResponseEntity<ApiResponse<String>>> result = userService.addUser(request);

        // Then
        assertEquals(HttpStatus.CREATED, result.join().getStatusCode());
    }

    @Test
    void testFindById_UserFound() {
        // Given
        User user = new User();
        user.setUid(1L);
        user.setUName("testUser");
        user.setURole("ROLE_USER");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        CompletableFuture<ResponseEntity<ApiResponse<UserDto>>> result = userService.findById(1L);

        // Then
        assertEquals(HttpStatus.OK, result.join().getStatusCode());
        assertEquals("testUser", result.join().getBody().getData().getUName());
    }

    @Test
    void testFindById_UserNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        CompletableFuture<ResponseEntity<ApiResponse<UserDto>>> result = userService.findById(1L);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, result.join().getStatusCode());
    }
}
