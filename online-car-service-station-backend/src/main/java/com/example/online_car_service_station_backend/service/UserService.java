package com.example.online_car_service_station_backend.service;

import com.example.online_car_service_station_backend.dto.UserResponse;
import com.example.online_car_service_station_backend.dto.UserUpdateRequest;
import com.example.online_car_service_station_backend.model.Admin;
import com.example.online_car_service_station_backend.model.Customer;
import com.example.online_car_service_station_backend.model.User;
import com.example.online_car_service_station_backend.repository.AdminRepository;
import com.example.online_car_service_station_backend.repository.CustomerRepository;
import com.example.online_car_service_station_backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Justification: CRITICAL FIX. The mapping logic is now more explicit.
    // It first checks if the user has a customer profile. If not, it checks for an
    // admin profile. This ensures all profile details are correctly fetched.
    private UserResponse mapToResponse(User user) {
        String firstName = null;
        String lastName = null;
        String address = null;
        String profileImageUrl = null;

        Optional<Customer> customerOptional = customerRepository.findByUserId(user.getId());
        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();
            firstName = customer.getFirstName();
            lastName = customer.getLastName();
            address = customer.getAddress();
            profileImageUrl = customer.getProfileImageUrl();
        } else {
            Optional<Admin> adminOptional = adminRepository.findByUserId(user.getId());
            if (adminOptional.isPresent()) {
                Admin admin = adminOptional.get();
                firstName = admin.getFirstName();
                lastName = admin.getLastName();
                profileImageUrl = admin.getProfileImageUrl();
            }
        }

        List<String> roles = user.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toList());

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                firstName,
                lastName,
                address,
                user.getPhone(),
                profileImageUrl,
                roles
        );
    }

    @Transactional
    public UserResponse getCurrentUser(UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToResponse(user);
    }

    @Transactional
    public UserResponse updateCustomerProfile(UserDetails userDetails, UserUpdateRequest updateRequest) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Customer customer = customerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Customer profile not found"));

        if (updateRequest.getUsername() != null) user.setUsername(updateRequest.getUsername());
        if (updateRequest.getEmail() != null) user.setEmail(updateRequest.getEmail());
        if (updateRequest.getPassword() != null) user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
        if (updateRequest.getPhone() != null) user.setPhone(updateRequest.getPhone());

        if (updateRequest.getFirstName() != null) customer.setFirstName(updateRequest.getFirstName());
        if (updateRequest.getLastName() != null) customer.setLastName(updateRequest.getLastName());
        if (updateRequest.getAddress() != null) customer.setAddress(updateRequest.getAddress());
        if (updateRequest.getProfileImageUrl() != null) customer.setProfileImageUrl(updateRequest.getProfileImageUrl());

        userRepository.save(user);
        customerRepository.save(customer);

        return mapToResponse(user);
    }

    @Transactional
    public UserResponse updateAdminProfile(UserDetails userDetails, UserUpdateRequest updateRequest) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Admin admin = adminRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Admin profile not found"));

        if (updateRequest.getUsername() != null) user.setUsername(updateRequest.getUsername());
        if (updateRequest.getEmail() != null) user.setEmail(updateRequest.getEmail());
        if (updateRequest.getPassword() != null) user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
        if (updateRequest.getPhone() != null) user.setPhone(updateRequest.getPhone());

        if (updateRequest.getFirstName() != null) admin.setFirstName(updateRequest.getFirstName());
        if (updateRequest.getLastName() != null) admin.setLastName(updateRequest.getLastName());
        if (updateRequest.getProfileImageUrl() != null) admin.setProfileImageUrl(updateRequest.getProfileImageUrl());

        userRepository.save(user);
        adminRepository.save(admin);

        return mapToResponse(user);
    }
}