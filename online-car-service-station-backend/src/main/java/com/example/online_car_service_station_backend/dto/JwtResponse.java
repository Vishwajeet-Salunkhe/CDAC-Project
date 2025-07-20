package com.example.online_car_service_station_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// Justification: This DTO defines the structure of the response sent back to the client
// after successful user login. It contains the JWT token and essential user details.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String type = "Bearer"; // Justification: Standard token type prefix.
    private Long id;
    private String username;
    private String email;
    private String profileImageUrl; // <-- NEW FIELD: Add the profile image URL
    private List<String> roles; // Justification: User's roles for frontend-side conditional rendering.

    // Justification: Custom constructor for convenience, excluding the 'type' field
    // so it defaults to "Bearer".
    public JwtResponse(String accessToken, Long id, String username, String email, String profileImageUrl, List<String> roles) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.email = email;
        this.profileImageUrl = profileImageUrl; // <-- NEW FIELD: Populate the image URL
        this.roles = roles;
    }
}