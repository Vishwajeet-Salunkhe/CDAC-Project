package com.example.online_car_service_station_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// Justification: This class provides general application-level configuration beans.
// @Configuration marks it as a source of Spring beans.
@Configuration
public class AppConfig {

    // Justification: Defines the PasswordEncoder bean used throughout the application
    // for hashing passwords. BCryptPasswordEncoder is a strong, industry-standard
    // hashing algorithm for passwords. It's crucial for security.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}