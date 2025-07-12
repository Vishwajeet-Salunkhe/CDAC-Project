package com.example.online_car_service_station_backend.repository;

import com.example.online_car_service_station_backend.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Justification: Provides CRUD operations for the Customer entity, allowing management
// of customer-specific profiles in the database.
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    // Justification: Useful for directly finding a customer by their associated user's ID.
    Optional<Customer> findByUserId(Long userId);
}