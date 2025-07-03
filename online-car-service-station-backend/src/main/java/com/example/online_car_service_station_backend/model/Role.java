package com.example.online_car_service_station_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Justification: The Role entity is fundamental for implementing role-based access control (RBAC).
// It allows us to define distinct roles (like ADMIN, CUSTOMER) and associate them with users.
// Using a separate entity makes the roles configurable and extensible.
@Entity
@Table(name = "roles")
@Data // Lombok: Generates getters, setters, toString(), equals(), and hashCode()
@NoArgsConstructor // Lombok: Generates a no-argument constructor
@AllArgsConstructor // Lombok: Generates a constructor with all fields
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrementing primary key
    private Integer id;

    // Justification: Stores the name of the role (e.g., "ROLE_ADMIN", "ROLE_CUSTOMER").
    // @Enumerated(EnumType.STRING) ensures that the enum name (string) is stored in the DB,
    // making the database more readable and maintainable than storing integer ordinals.
    // @Column(length = 20, unique = true) ensures the role name is unique and has a max length.
    @Enumerated(EnumType.STRING)
    @Column(length = 20, unique = true, nullable = false)
    private ERole name;
}