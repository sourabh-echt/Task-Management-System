package com.echt.task_management_system.repository;

import com.echt.task_management_system.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository
        extends JpaRepository<User, UUID> {

    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by username
     */
    Optional<User> findByUsername(String username);

    /**
     * Check if email already exists
     */
    boolean existsByEmail(String email);

    /**
     * Check if username already exists
     */
    boolean existsByUsername(String username);
}