package com.example.online_car_service_station_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// Justification: This DTO is used to update a customer's profile. It contains fields
// that the customer is allowed to change and includes validation rules.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {
    private String firstName;
    private String lastName;
    private String address;

    // Justification: The phone number is validated to ensure it's a 10-digit number.
    @Pattern(regexp="\\d{10}", message="Phone number must be a 10-digit number")
    private String phone;

    // Justification: The email is validated for a correct email format.
    @Email(message="Email should be valid")
    private String email;

    private String profileImageUrl;
}