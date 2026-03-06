package com.reminder.repository;

import com.reminder.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, Integer> {

    @Query("SELECT u FROM User u where u.email =: email")
    Optional<User> findByEmail(@Param("email") String email);
}
