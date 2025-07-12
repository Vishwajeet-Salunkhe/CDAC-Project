package com.example.online_car_service_station_backend.repository;

import com.example.online_car_service_station_backend.model.ERole;
import com.example.online_car_service_station_backend.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Justification: JpaRepository provides out-of-the-box CRUD operations for the Role entity.
// By extending it, we get common database operations (save, findById, findAll, delete)
// without writing boilerplate code, significantly speeding up data access layer development.
// @Repository annotation marks this interface as a Spring Data JPA repository.
@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    // Justification: This custom method allows us to retrieve a Role by its ERole name.
    // This is crucial for assigning roles to users based on their string representation
    // (e.g., when registering a new user as 'ROLE_CUSTOMER'). Optional handles cases
    // where a role might not be found, preventing NullPointerExceptions.
    Optional<Role> findByName(ERole name);
}