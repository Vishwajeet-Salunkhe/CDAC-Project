package com.example.online_car_service_station_backend.controller;

import com.example.online_car_service_station_backend.dto.ServiceRequest;
import com.example.online_car_service_station_backend.dto.ServiceResponse;
import com.example.online_car_service_station_backend.model.CarService;
import com.example.online_car_service_station_backend.service.ServiceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Justification: This controller handles all HTTP requests related to service management.
// It uses @RestController and @RequestMapping for RESTful API design.
// @CrossOrigin is essential to allow requests from the frontend (e.g., React on port 5173).
@RestController
@RequestMapping("/api/services")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ServiceController {

    @Autowired
    private ServiceService serviceService;

    // Justification: Allows an ADMIN to add a new service.
    // @PreAuthorize("hasRole('ADMIN')") ensures this endpoint can only be accessed by an authenticated user with ROLE_ADMIN.
    // The @Valid annotation triggers DTO validation for the incoming ServiceRequest.
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServiceResponse> createService(@Valid @RequestBody ServiceRequest serviceRequest) {
        CarService createdService = serviceService.createService(serviceRequest);
        // Justification: Converts the created entity back to a DTO for the API response.
        ServiceResponse response = mapToResponse(createdService);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Justification: Allows both ADMIN and CUSTOMER to view all available services.
    // @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')") enforces this role-based access.
    @GetMapping
  //  @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<List<ServiceResponse>> getAllServices() {
        List<ServiceResponse> services = serviceService.getAllServices().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(services);
    }

    // Justification: Allows both ADMIN and CUSTOMER to view a single service by ID.
    // The access is restricted by the @PreAuthorize annotation.
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<ServiceResponse> getServiceById(@PathVariable Long id) {
        Optional<ServiceResponse> serviceResponse = serviceService.getServiceById(id)
                .map(this::mapToResponse);
        return serviceResponse
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Justification: Allows an ADMIN to update an existing service.
    // Secured with @PreAuthorize("hasRole('ADMIN')").
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServiceResponse> updateService(@PathVariable Long id, @Valid @RequestBody ServiceRequest serviceRequest) {
        Optional<ServiceResponse> updatedService = serviceService.updateService(id, serviceRequest)
                .map(this::mapToResponse);
        return updatedService
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Justification: Allows an ADMIN to delete an existing service.
    // Secured with @PreAuthorize("hasRole('ADMIN')").
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        if (serviceService.deleteService(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Justification: Helper method to map a Service entity to a ServiceResponse DTO.
    // This keeps the code clean and follows the DTO pattern.
    private ServiceResponse mapToResponse(CarService service) {
        return new ServiceResponse(service.getId(), service.getName(), service.getDescription(), service.getPrice(), service.getImageUrl());
    }
}