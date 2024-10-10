package com.kadir.abdul.Twitter_App.repository;

import java.util.List;
import java.util.Optional;
///import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import com.kadir.abdul.Twitter_App.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Async
    @Query("SELECT u FROM User u WHERE u.uName = :uName")
    CompletableFuture<Optional<User>> findByUsername(@Param("uName") String uName);

    @Async
    @Query("SELECT u FROM User u WHERE u.uRole = :role")
    List<User> findUserByRole(@Param("role") String role);

}
