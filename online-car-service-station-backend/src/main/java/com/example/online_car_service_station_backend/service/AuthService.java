package com.example.online_car_service_station_backend.service;

import com.example.online_car_service_station_backend.dto.RegisterRequest;
import com.example.online_car_service_station_backend.model.Customer;
import com.example.online_car_service_station_backend.model.ERole;
import com.example.online_car_service_station_backend.model.Role;
import com.example.online_car_service_station_backend.model.User;
import com.example.online_car_service_station_backend.model.Admin;
import com.example.online_car_service_station_backend.repository.AdminRepository;
import com.example.online_car_service_station_backend.repository.CustomerRepository;
import com.example.online_car_service_station_backend.repository.RoleRepository;
import com.example.online_car_service_station_backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

// Justification: This service class encapsulates the business logic for user registration and related authentication tasks.
// Separating logic into a service layer (Service Layer Pattern) promotes modularity, reusability, and testability,
// keeping controllers focused on handling HTTP requests.
@Service
public class AuthService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    AdminRepository adminRepository; // <-- CRITICAL: Autowire the AdminRepository


    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    PasswordEncoder encoder;

    // Justification: @Transactional ensures that the entire registration process (creating user and customer)
    // is an atomic operation. If any part fails (e.g., database constraint violation), all changes are rolled back,
    // maintaining data consistency. This is critical when multiple DAOs (UserRepository, CustomerRepository) are involved.
    @Transactional
    public User registerCustomer(RegisterRequest registerRequest) {
        // Justification: Check for existing username to prevent duplicates, as usernames must be unique.
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Error: Username is already taken!");
        }

        // Justification: Check for existing email to prevent duplicates, as emails must be unique.
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        // 1. Create new User's account
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        // Justification: Encode the password before saving to the database for security.
        user.setPassword(encoder.encode(registerRequest.getPassword()));
        user.setPhone(registerRequest.getPhone());

        // Justification: Assign the default role for customer registration.
        // As per instructions, users cannot register as admin through this endpoint.
        Set<Role> roles = new HashSet<>();
        Role customerRole = roleRepository.findByName(ERole.ROLE_CUSTOMER)
                .orElseThrow(() -> new RuntimeException("Error: Customer Role not found."));
        roles.add(customerRole);
        user.setRoles(roles);

        // 2. Create associated Customer profile
        Customer customerProfile = new Customer();
        customerProfile.setFirstName(registerRequest.getFirstName());
        customerProfile.setLastName(registerRequest.getLastName());
        customerProfile.setAddress(registerRequest.getAddress());
        customerProfile.setProfileImageUrl(registerRequest.getProfileImageUrl());

        // Justification: Establish the bidirectional relationship. This is essential for
        // JPA's cascading persistence (@OneToOne(mappedBy="user", cascade=...) on User entity)
        // and for @MapsId on the Customer entity.
        user.setCustomerProfile(customerProfile); // Link User to Customer profile
        customerProfile.setUser(user);             // Link Customer profile back to User

        // Justification: Save the User. Due to cascade configuration in User entity,
        // the associated Customer profile will also be saved automatically.
        User savedUser = userRepository.save(user);

        return savedUser; // Return the saved user entity (or a DTO, depending on requirements)
    }

    // Justification: This new helper method correctly retrieves the profile image URL.
    // It now checks both the Customer and Admin repositories to find the correct profile.
    @Transactional
    public String getProfileImageUrl(UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<Customer> customerOptional = customerRepository.findByUserId(user.getId());
        if (customerOptional.isPresent()) {
            return customerOptional.get().getProfileImageUrl();
        }

        Optional<Admin> adminOptional = adminRepository.findByUserId(user.getId());
        if (adminOptional.isPresent()) {
            return adminOptional.get().getProfileImageUrl();
        }

        // If the user has no profile, return a default URL.
        return null;
    }


    // Justification: Constraint: Only admin can add another admin into System.
    // This method will be called only from an admin-only endpoint.
    @Transactional
    public User registerAdmin(RegisterRequest registerRequest) {
        // Justification: Implement checks similar to customer registration.
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Error: Username is already taken!");
        }
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(encoder.encode(registerRequest.getPassword()));
        user.setPhone(registerRequest.getPhone());

        Set<Role> roles = new HashSet<>();
        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Error: Admin Role not found."));
        roles.add(adminRole);
        user.setRoles(roles);

        Admin adminProfile = new Admin();
        adminProfile.setFirstName(registerRequest.getFirstName());
        adminProfile.setLastName(registerRequest.getLastName());

        user.setAdminProfile(adminProfile);
        adminProfile.setUser(user);

        User savedUser = userRepository.save(user);
        return savedUser;
    }


}