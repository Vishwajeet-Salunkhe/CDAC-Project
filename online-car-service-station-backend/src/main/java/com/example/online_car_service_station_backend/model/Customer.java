package com.example.online_car_service_station_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// Justification: The Customer entity holds attributes specific to a customer,
// such as their name, address, and profile image URL.
// It extends the base User's identity and provides additional, role-specific details.
@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
// Justification: CRITICAL FIX. We explicitly exclude the 'user' field from equals() and hashCode()
// generation. The primary key ('id') already provides a unique identity, and including the
// 'user' field creates a circular dependency and an infinite recursion.
@EqualsAndHashCode(callSuper = false, exclude = "user")
public class Customer {
    // Justification: Using @Id and @MapsId creates a shared primary key with the User entity.
    // This implies a One-to-One relationship where the Customer's ID is the same as its User's ID.
    // This setup is efficient as it avoids redundant ID columns and clearly links customer details to a user account.
    @Id
    private Long id;

    private String firstName;
    private String lastName;
    private String address;

    // Justification: As per image requirement "profile management (CRWD) with image".
    // Stores the URL of the customer's profile picture.
    private String profileImageUrl;

    // Justification: Defines the One-to-One relationship with the User entity.
    // @JoinColumn specifies the foreign key column (`id` in `customers` table which references `id` in `users` table).
    // @MapsId indicates that the primary key of this entity is also a foreign key to the User entity.
    @OneToOne
    @MapsId // Ensures the primary key of Customer is mapped from the primary key of User
    @JoinColumn(name = "id")
    private User user; // Reference to the associated User entity
}