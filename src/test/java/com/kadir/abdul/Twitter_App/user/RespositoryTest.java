package com.kadir.abdul.Twitter_App.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.kadir.abdul.Twitter_App.dto.AddUserRequest;
import com.kadir.abdul.Twitter_App.dto.UserDto;
import com.kadir.abdul.Twitter_App.entity.User;
import com.kadir.abdul.Twitter_App.repository.UserRepository;
import com.kadir.abdul.Twitter_App.response.ApiResponse;
import com.kadir.abdul.Twitter_App.service.UserService;
import com.kadir.abdul.Twitter_App.service.Imlp.UserServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class RespositoryTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

    }

    @DisplayName("Save user test")
    @Test
    public void testSavedUser() throws InterruptedException, ExecutionException {
        // Given - Set Up
        AddUserRequest user = new AddUserRequest();
        user.setUName("abdul");
        user.setURole("user");

        User savedUser = new User();
        savedUser.setUName("Abdul");
        savedUser.setURole("user");

        ApiResponse<String> response = new ApiResponse<>();
        response.setResponseMessage("User saved successfully");

        ResponseEntity<ApiResponse<String>> responseEntity = new ResponseEntity<>(response, HttpStatus.CREATED);

        given(userService.addUser(user)).willReturn(CompletableFuture.completedFuture(responseEntity));

        // When - Action
        CompletableFuture<ResponseEntity<ApiResponse<String>>> foundUserFutre = userService.addUser(user);
        ResponseEntity<ApiResponse<String>> foundUserResponse = foundUserFutre.get();

        assertThat(foundUserResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(foundUserResponse.getBody()).isNotNull();
        assertThat(foundUserResponse.getBody().getResponseMessage()).isEqualTo("User saved successfully");
    

    }

   
    public void list_Users_By_Given_Role() {
        User user = new User();
        user.setUid(1L);
        user.setUName("Abdul");
        user.setURole("user");

        User user1 = new User();
        user.setUid(2L);
        user.setUName("Kadir");
        user.setURole("user");

        List<User> users = new ArrayList<>();
        users.add(user);
        users.add(user1);

        when(userRepository.findUserByRole("user")).thenReturn(users);

        // Act: Call the method under test
        CompletableFuture<ResponseEntity<ApiResponse<List<UserDto>>>> responseFuture = userService
                .findUserListByRole("user");

        // Await the response
        ResponseEntity<ApiResponse<List<UserDto>>> response = responseFuture.join();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        // assertThat(response.getBody().getData().size());
        assertEquals("Abdul", response.getBody().getData().get(0).getUName());
        assertEquals("user", response.getBody().getData().get(1).getURole());

    }

}
