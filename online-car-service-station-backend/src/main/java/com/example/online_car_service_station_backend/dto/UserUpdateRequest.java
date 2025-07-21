package com.example.online_car_service_station_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// Justification: This DTO is used to update a user's profile. It contains the same
// validation rules as registration but with key differences: the username and email
// can be optional for partial updates, and the password is not required.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Username must be alphanumeric with no spaces or special characters")
    private String username;

    @Size(max = 50, message = "Email must be less than 50 characters")
    @Email(message = "Email should be valid")
    private String email;

    @Size(min = 6, max = 40, message = "Password must be between 6 and 40 characters")
    private String password; // Optional for update

    private String firstName;
    private String lastName;
    private String address;

    @Pattern(regexp="\\d{10}", message="Phone number must be a 10-digit number")
    private String phone;

    private String profileImageUrl;
}