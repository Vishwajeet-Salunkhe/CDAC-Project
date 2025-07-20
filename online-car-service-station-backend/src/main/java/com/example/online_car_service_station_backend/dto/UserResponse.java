package com.example.online_car_service_station_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// Justification: This DTO is used to send the authenticated user's profile details
// to the frontend. It is a secure and clean representation of the user data,
// without exposing sensitive information like the password.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String address;
    private String phone;
    private String profileImageUrl;
    private List<String> roles;
}