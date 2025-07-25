package com.example.online_car_service_station_backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

// Justification: This DTO represents the data required to create or update a service.
// Using a DTO prevents exposing the internal Service entity structure directly.
// The validation annotations ensure that incoming data is valid before processing.
@Data
public class ServiceRequest {
    @NotBlank(message = "Service name cannot be blank")
    private String name;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    @NotNull(message = "Price cannot be null")
    // Justification: Price must be a positive value.
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    private String imageUrl;
}