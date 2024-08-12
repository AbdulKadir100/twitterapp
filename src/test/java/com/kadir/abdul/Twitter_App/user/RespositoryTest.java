package com.kadir.abdul.Twitter_App.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.kadir.abdul.Twitter_App.dto.UserDto;
import com.kadir.abdul.Twitter_App.entity.User;
import com.kadir.abdul.Twitter_App.repository.UserRepository;
import com.kadir.abdul.Twitter_App.response.ApiResponse;
import com.kadir.abdul.Twitter_App.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class RespositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Test
    public void testSavedUser() {
        User user = new User();
        user.setUid(1L);
        user.setUName("Abdul");
        user.setURole("user");

        userRepository.save(user);

        Optional<User> foundUser = userRepository.findById(1L);

        assertThat(foundUser.isPresent()).isTrue();
        assertThat(foundUser.get().getUName()).isEqualTo("Abdul");
        assertThat(foundUser.get().getURole()).isEqualTo("user");

    }

    @Test
    public void list_Users_By_Given_Role() {
        List<User> mockUsers = Arrays.asList(
                new User(1L, "Abdul", "user"),
                new User(2L, "Abdul", "user"));

        when(userRepository.findUserByRole("user")).thenReturn(mockUsers);

        // Act: Call the method under test
        CompletableFuture<ResponseEntity<ApiResponse<List<UserDto>>>> responseFuture = userService
                .findUserListByRole("user");

        // Await the response
        ResponseEntity<ApiResponse<List<UserDto>>> response = responseFuture.join();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().getData().size());
        assertEquals("Abdul", response.getBody().getData().get(0).getUName());
        assertEquals("user", response.getBody().getData().get(0).getURole());

    }

}
