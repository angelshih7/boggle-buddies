package com.example.Boggle.repository;

import com.example.Boggle.Model.Tables.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


/**
 * Repository for User entities.
 *
 * Provides standard CRUD operations and query methods for
 * locating users by email or username, as well as checking
 * whether a username is already taken.
 */
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}
