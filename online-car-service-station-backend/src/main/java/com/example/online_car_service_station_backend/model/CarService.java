package com.example.online_car_service_station_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

// Justification: The Service entity is a fundamental part of the application's domain model.
// It represents a specific type of car service offered by the garage.
// This entity is managed by JPA and mapped to the 'services' table in the database.
@Entity
@Table(name = "services")
@Data // Lombok: Generates getters, setters, toString(), equals(), and hashCode()
@NoArgsConstructor // Lombok: Generates a no-argument constructor (required by JPA)
@AllArgsConstructor // Lombok: Generates a constructor with all fields
public class CarService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrementing primary key
    private Long id;

    // Justification: The name of the service, e.g., "Oil Change". Must be unique to prevent duplicates.
    // @Column(unique = true, nullable = false) enforces this database constraint.
    @Column(unique = true, nullable = false)
    private String name;

    // Justification: A detailed description of the service.
    @Lob // Justification: @Lob is used for large character objects, suitable for a text block like a description.
    private String description;

    // Justification: The price of the service. Using BigDecimal for monetary values is best practice
    // to avoid floating-point precision errors.
    @Column(nullable = false, precision = 10, scale = 2) // Precision and scale for monetary values
    private BigDecimal price;

    // Justification: An image URL to display with the service on the frontend.
    // This addresses the 'with image' requirement from the initial project notes.
    private String imageUrl;

    // Additional fields could be added here, such as 'durationInMinutes', 'isAvailable', etc.

    // Justification: This completes the many-to-many relationship with Booking through the BookingService entity.
    // 'mappedBy' indicates that the other side (BookingService entity) is the owner of the relationship.
    // This allows Hibernate to correctly manage the relationship from both sides.
    @OneToMany(mappedBy = "carService", fetch = FetchType.LAZY)
    private Set<BookedService> bookingServices = new HashSet<>();
}