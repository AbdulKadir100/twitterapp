package com.kadir.abdul.Twitter_App.user;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.kadir.abdul.Twitter_App.dto.AddUserRequest;
import com.kadir.abdul.Twitter_App.dto.UserDto;
import com.kadir.abdul.Twitter_App.entity.User;
import com.kadir.abdul.Twitter_App.repository.UserRepository;
import com.kadir.abdul.Twitter_App.response.ApiResponse;
import com.kadir.abdul.Twitter_App.service.Imlp.UserServiceImpl;
import com.kadir.abdul.Twitter_App.utils.MessageUtil;

@SpringBootTest
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private AddUserRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        request = new AddUserRequest();
        request.setURole("User");
        request.setUName("Abdul Kadir");

    }

    @Test
    void validateUserExists_userExists() throws Exception {
        User user = new User();
        user.setUid(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        CompletableFuture<User> result = userService.validateUserExists(1L);

        assertEquals(user, result.get());
    }

    @Test
    void validateUserExists_userNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        CompletableFuture<User> result = userService.validateUserExists(1L);

        ExecutionException exception = assertThrows(ExecutionException.class, () -> {
            result.get();
        });

        assertTrue(exception.getCause() instanceof RuntimeException);
        assertTrue(exception.getCause().getMessage().contains("User with ID 1 not found"));
    }

    @Test
    public void testSaveNewUser_Success() throws Exception {
        User user = User.builder()
                .uRole(request.getURole())
                .uName(request.getUName())
                .build();

        when(userRepository.save(any(User.class))).thenReturn(user);

        CompletableFuture<ResponseEntity<ApiResponse<String>>> response = userService.saveNewUser(request);
        ResponseEntity<ApiResponse<String>> result = response.get();

        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(MessageUtil.SUCCESS, result.getBody().getResponseMessage());
        assertEquals(HttpStatus.CREATED.value(), result.getBody().getResponseCode());
        assertEquals(MessageUtil.USER_ADDED, result.getBody().getData());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testSaveNewUser_Failure() throws Exception {

        AddUserRequest request = new AddUserRequest();

        request.setURole("User");
        request.setUName("Abdul Kadir");

        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        CompletableFuture<ResponseEntity<ApiResponse<String>>> response = userService.saveNewUser(request);

        ExecutionException exception = assertThrows(ExecutionException.class, () -> {
            response.get();
        });

        assertTrue(exception.getCause() instanceof RuntimeException);
        assertEquals("Database error", exception.getCause().getMessage());
    }

    @Test
    public void testFindUserListByRole_Success() throws Exception {
        String role = "Admin";

        List<User> users = Arrays.asList(
                new User(1L, "John Doe", role),
                new User(2L, "Jane Doe", role));

        when(userRepository.findUserByRole(role)).thenReturn(users);

        CompletableFuture<ResponseEntity<ApiResponse<List<UserDto>>>> responseFuture = userService
                .findUserListByRole(role);
        ResponseEntity<ApiResponse<List<UserDto>>> response = responseFuture.get();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MessageUtil.SUCCESS, response.getBody().getResponseMessage());
        assertEquals(HttpStatus.OK.value(), response.getBody().getResponseCode());

        List<UserDto> userDtos = response.getBody().getData();
        assertNotNull(userDtos);
        assertEquals(2, userDtos.size());
        assertEquals("John Doe", userDtos.get(0).getUName());
        assertEquals("Jane Doe", userDtos.get(1).getUName());
        verify(userRepository, times(1)).findUserByRole(role);
    }


    @Test
    public void testFindUserListByRole_NotFound() throws Exception {
        String role = "Admin";
        when(userRepository.findUserByRole(role)).thenReturn(Collections.emptyList());
        CompletableFuture<ResponseEntity<ApiResponse<List<UserDto>>>> responseFuture = userService
                .findUserListByRole(role);
        ResponseEntity<ApiResponse<List<UserDto>>> response = responseFuture.get();
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("No users found for the given role", response.getBody().getResponseMessage());
        assertNull(response.getBody().getData());
        verify(userRepository, times(1)).findUserByRole(role);
    }

    @Test
    public void testFindUserListByRole_Exception() throws Exception {
        String role = "Admin";
        when(userRepository.findUserByRole(role)).thenThrow(new RuntimeException("Database error"));
        CompletableFuture<ResponseEntity<ApiResponse<List<UserDto>>>> responseFuture = userService
                .findUserListByRole(role);
        ExecutionException exception = assertThrows(ExecutionException.class, () -> {
            responseFuture.get();
        });
        assertTrue(exception.getCause() instanceof RuntimeException);
        assertEquals("Database error", exception.getCause().getMessage());
        verify(userRepository, times(1)).findUserByRole(role);
    }
}
