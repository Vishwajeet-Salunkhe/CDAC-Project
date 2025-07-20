package com.example.online_car_service_station_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

// Justification: This DTO encapsulates the data required for a new user registration.
// It includes validation annotations to ensure data integrity and prevent common issues
// like blank fields or invalid email formats directly at the API boundary.
@Data
public class RegisterRequest {
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    @NotBlank(message = "Email cannot be blank")
    @Size(max = 50, message = "Email must be less than 50 characters")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, max = 40, message = "Password must be between 6 and 40 characters")
    private String password;

    @NotBlank(message = "First name cannot be blank")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    private String lastName;

    private String address; // Optional
    // Justification: CRITICAL FIX. The @Pattern annotation uses a regular expression
    // to enforce that the phone number field must contain exactly 10 digits.
    // This is a robust server-side validation that prevents invalid data from being
    // saved to the database.
    @Pattern(regexp="\\d{10}", message="Phone number must be a 10-digit number")
    private String phone;

    private String profileImageUrl; // Optional
}