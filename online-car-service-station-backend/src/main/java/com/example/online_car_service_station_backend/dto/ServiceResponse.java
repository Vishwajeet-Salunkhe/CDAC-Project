package com.example.online_car_service_station_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// Justification: This DTO represents the data returned to the client when a service is
// retrieved. It's good practice to use a dedicated response DTO to control what data
// is exposed from the backend, potentially hiding sensitive or unnecessary fields.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
}