package com.kadir.abdul.Twitter_App.user;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.kadir.abdul.Twitter_App.entity.User;
import com.kadir.abdul.Twitter_App.repository.UserRepository;


import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class RespositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSavedUser(){
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

}
