package com.example.online_car_service_station_backend.service;

import com.example.online_car_service_station_backend.dto.ServiceRequest;
import com.example.online_car_service_station_backend.model.CarService;
import com.example.online_car_service_station_backend.repository.ServiceRepository;
import jakarta.transaction.Transactional; // Use jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// Justification: This service class contains the business logic for managing services.
// It acts as an intermediary between the controller and the repository, providing a clean
// and testable layer for all service-related operations.
@Service
public class ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;

    // Justification: @Transactional ensures that the entire method runs within a single database transaction.
    // This is good practice for write operations to ensure data integrity.
    @Transactional
    public CarService createService(ServiceRequest serviceRequest) {
        // Justification: Converts the DTO to the entity. This is a best practice for clean architecture.
        CarService newService = new CarService();
        newService.setName(serviceRequest.getName());
        newService.setDescription(serviceRequest.getDescription());
        newService.setPrice(serviceRequest.getPrice());
        newService.setImageUrl(serviceRequest.getImageUrl());
        return serviceRepository.save(newService);
    }

    // Justification: Read-only transactions are more performant and prevent accidental data modification.
    // This method retrieves all services from the database.
    @Transactional
    public List<CarService> getAllServices() {
        return serviceRepository.findAll();
    }

    // Justification: Retrieves a single service by its ID. Optional<Service> handles cases
    // where the service is not found, preventing NullPointerExceptions.
    @Transactional
    public Optional<CarService> getServiceById(Long id) {
        return serviceRepository.findById(id);
    }

    // Justification: Updates an existing service. It finds the service by ID, updates its fields,
    // and saves it. @Transactional ensures the operation is atomic.
    @Transactional
    public Optional<CarService> updateService(Long id, ServiceRequest serviceRequest) {
        return serviceRepository.findById(id).map(existingService -> {
            existingService.setName(serviceRequest.getName());
            existingService.setDescription(serviceRequest.getDescription());
            existingService.setPrice(serviceRequest.getPrice());
            existingService.setImageUrl(serviceRequest.getImageUrl());
            return serviceRepository.save(existingService);
        });
    }

    // Justification: Deletes a service by its ID. It first checks if the service exists
    // to avoid deleting a non-existent entry. @Transactional ensures this operation is atomic.
    @Transactional
    public boolean deleteService(Long id) {
        return serviceRepository.findById(id).map(service -> {
            serviceRepository.delete(service);
            return true;
        }).orElse(false);
    }
}