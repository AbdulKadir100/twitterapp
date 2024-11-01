package com.kadir.abdul.Twitter_App.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import com.kadir.abdul.Twitter_App.entity.User;
import com.kadir.abdul.Twitter_App.repository.UserRepository;
import com.kadir.abdul.Twitter_App.service.UserService;
import com.kadir.abdul.Twitter_App.service.Imlp.UserServiceImpl;

@SpringBootTest
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

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

}
