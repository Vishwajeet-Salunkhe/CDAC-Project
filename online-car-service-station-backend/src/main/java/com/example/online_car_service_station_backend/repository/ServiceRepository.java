package com.example.online_car_service_station_backend.repository;

import com.example.online_car_service_station_backend.model.CarService; // Import the new entity name
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// Justification: The repository interface now correctly references the renamed CarService entity.
public interface ServiceRepository extends JpaRepository<CarService, Long> {
}