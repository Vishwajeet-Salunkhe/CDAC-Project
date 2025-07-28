package com.example.online_car_service_station_backend.controller;

import com.example.online_car_service_station_backend.dto.LoginRequest; // Will create this DTO
import com.example.online_car_service_station_backend.dto.JwtResponse; // Will create this DTO
import com.example.online_car_service_station_backend.dto.RegisterRequest;
import com.example.online_car_service_station_backend.security.jwt.JwtUtils;
import com.example.online_car_service_station_backend.security.services.UserDetailsImpl;
import com.example.online_car_service_station_backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid; // For input validation

import java.util.List;
import java.util.stream.Collectors;

// Justification: Handles authentication-related API endpoints like login.
// @RestController combines @Controller and @ResponseBody, meaning return values are directly bound to the web response body.
// @RequestMapping("/api/auth") maps all methods in this controller to paths starting with /api/auth.
// @CrossOrigin enables CORS, allowing your React frontend (on a different port/domain) to send requests.
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600) // Align with CorsConfigurationSource
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    // Justification: Handles user login. It takes a LoginRequest (username/password),
    // authenticates it using AuthenticationManager, generates a JWT, and returns it.
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        // Justification: Attempts to authenticate the user using the provided username and password.
        // If authentication fails (e.g., bad credentials), Spring Security will throw an exception.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        // Justification: If authentication is successful, set the Authentication object in the SecurityContext.
        // This is important for subsequent security checks within the current request thread.
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Justification: Generate a JWT for the authenticated user. This token will be sent back
        // to the client and used in subsequent requests for authentication and authorization.
        String jwt = jwtUtils.generateJwtToken(authentication);

        // Justification: Get the authenticated user's details (including ID, username, email, roles)
        // from the Authentication object to build the response.
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // Justification: Call the new service method to get the profile image URL.
        String profileImageUrl = authService.getProfileImageUrl(userDetails);

        // Justification: Extract roles into a list of strings for the JWT response.
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // Justification: Return a JWTResponse containing the token, user ID, username, email, and roles.
        // This response is what your frontend will use.
        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                profileImageUrl,  // <-- NEW FIELD: Pass the profile image URL
                roles));
    }

    // Add /register endpoints later in the "User Management" module.
    @Autowired
    AuthService authService; // Justification: Inject AuthService to handle registration logic.

    // Justification: This endpoint handles the registration of new customer users.
    // It's publicly accessible (`permitAll` in WebSecurityConfig).
    // It calls the AuthService to handle the actual user and customer profile creation.
    @PostMapping("/register/customer")
    public ResponseEntity<?> registerCustomer(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            authService.registerCustomer(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body("Customer registered successfully!");
        } catch (RuntimeException e) {
            // Justification: Catch specific exceptions (e.g., username/email already taken)
            // and return appropriate HTTP status codes and messages.
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}