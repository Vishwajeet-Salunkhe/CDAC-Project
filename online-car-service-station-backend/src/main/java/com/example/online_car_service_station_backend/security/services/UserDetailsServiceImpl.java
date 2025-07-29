package com.example.online_car_service_station_backend.security.services;

import com.example.online_car_service_station_backend.model.User;
import com.example.online_car_service_station_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Justification: This class implements Spring Security's UserDetailsService interface.
// Its primary role is to load user-specific data by username during the authentication process.
// @Service marks it as a Spring service component.
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    // Justification: Injected to access user data from the database.
    @Autowired
    UserRepository userRepository;

    // Justification: This is the core method of UserDetailsService.
    // It's called by Spring Security when a user attempts to log in.
    // @Transactional(readOnly = true) is applied for performance, as this operation only reads data.
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Justification: Retrieves the User entity from the database using the username.
        // If the user is not found, a UsernameNotFoundException is thrown, which Spring Security handles.
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        // Justification: Converts our application's User entity into the Spring Security-compatible
        // UserDetailsImpl object, which contains the user's ID, username, password, and authorities (roles).
        return UserDetailsImpl.build(user);
    }
}