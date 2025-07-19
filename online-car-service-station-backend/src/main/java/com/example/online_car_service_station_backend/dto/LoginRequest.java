package com.example.online_car_service_station_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// Justification: This DTO is used to encapsulate the request body for user login.
// It ensures that the username and password fields are present and not blank,
// which is enforced by Jakarta Validation annotations.
@Data
public class LoginRequest {
    @NotBlank // Justification: Ensures username is not null, empty, or whitespace only.
    private String username;

    @NotBlank // Justification: Ensures password is not null, empty, or whitespace only.
    private String password;
}