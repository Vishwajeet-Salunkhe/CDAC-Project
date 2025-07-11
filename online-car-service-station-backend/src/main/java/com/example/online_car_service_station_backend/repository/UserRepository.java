package com.example.online_car_service_station_backend.repository;

import com.example.online_car_service_station_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Justification: Similar to RoleRepository, JpaRepository provides CRUD operations for User.
// This is the primary interface for managing user data in the database.
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Justification: Essential for Spring Security's UserDetailsService to load a user
    // by their username during the login process. Optional handles non-existent users.
    Optional<User> findByUsername(String username);

    // Justification: Used during user registration to check if a username already exists,
    // preventing duplicate accounts.
    Boolean existsByUsername(String username);

    // Justification: Used during user registration to check if an email already exists,
    // as emails are typically unique per user.
    Boolean existsByEmail(String email);
}