package com.example.online_car_service_station_backend.controller;

import com.example.online_car_service_station_backend.dto.UserResponse;
import com.example.online_car_service_station_backend.dto.UserUpdateRequest;
import com.example.online_car_service_station_backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        UserResponse userResponse = userService.getCurrentUser(userDetails);
        return ResponseEntity.ok(userResponse);
    }

    // Justification: This new endpoint allows a customer to update their own profile.
    // It is secured to ensure only a customer can access it.
    @PutMapping("/me/customer")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<UserResponse> updateCustomerProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UserUpdateRequest updateRequest) {
        UserResponse updatedUser = userService.updateCustomerProfile(userDetails, updateRequest);
        return ResponseEntity.ok(updatedUser);
    }

    // Justification: This new endpoint allows an admin to update their own profile.
    // It is a separate endpoint, which is a cleaner design.
    @PutMapping("/me/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateAdminProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UserUpdateRequest updateRequest) {
        UserResponse updatedUser = userService.updateAdminProfile(userDetails, updateRequest);
        return ResponseEntity.ok(updatedUser);
    }
}