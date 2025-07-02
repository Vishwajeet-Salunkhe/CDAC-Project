package com.example.online_car_service_station_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

// Justification: The User entity is the core of our authentication system. It stores
// common user attributes like username, password, email, and phone.
// It serves as the base for both Admin and Customer specific details.
@Entity
@Table(name = "users")
@Data // Lombok
@NoArgsConstructor // Lombok
@AllArgsConstructor // Lombok
// Justification: CRITICAL FIX for StackOverflowError. We are explicitly excluding the
// bidirectional relationship fields (adminProfile and customerProfile) from the
// equals() and hashCode() generation to prevent an infinite recursion. Without this,
// calling .hashCode() on a User entity would recursively call .hashCode() on its
// linked profile, and vice-versa, causing the application to crash.
@EqualsAndHashCode(exclude = {"adminProfile", "customerProfile"})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrementing primary key
    private Long id;

    // Justification: Username is a unique identifier for login.
    // @Column(unique = true, nullable = false) enforces uniqueness and non-nullability at DB level.
    @Column(unique = true, nullable = false)
    private String username;

    // Justification: Stores the hashed password. It's crucial that passwords are NOT stored in plain text.
    // This field will hold the BCrypt encoded password.
    @Column(nullable = false)
    private String password;

    // Justification: Email is another unique identifier and is often used for communication.
    // Enforced unique and non-nullable.
    @Column(unique = true, nullable = false)
    private String email;

    // Justification: Optional contact information.
    private String phone;

    // Justification: This implements the Many-to-Many relationship between User and Role.
    // A user can have multiple roles (e.g., ADMIN and CUSTOMER in a more complex app, though here
    // typically one primary role), and a role can be assigned to many users.
    // @ManyToMany specifies the relationship.
    // fetch = FetchType.EAGER means roles are loaded immediately with the user, which is convenient
    // for security checks after user login.
    // @JoinTable defines the join table (`user_roles`) and the foreign key columns (`user_id`, `role_id`).
    // This is vital for implementing Spring Security's role-based authorization.
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>(); // Initialize to prevent NullPointerExceptions

    /**
     * Justification: Establishes the One-to-One relationship from User to Admin.
     * This side is declared as the 'owning' side of the relationship for persistence cascades.
     * - @OneToOne: Defines the relationship type.
     * - mappedBy = "user": Indicates that the 'Admin' entity is the owning side of the foreign key (i.e.,
     * the 'admins' table contains the foreign key column, mapped by its 'user' field).
     * - cascade = CascadeType.PERSIST, CascadeType.MERGE: This is CRITICAL. It means that if a User
     * entity is persisted (saved) or merged, its associated Admin entity (if set) will also be
     * persisted or merged automatically. This is usually what you want for `User` and `Admin`
     * creation simultaneously.
     * - orphanRemoval = true: If an Admin is disassociated from a User (e.g., `user.setAdmin(null)`),
     * the orphaned Admin record will be automatically deleted from the database.
     * - fetch = FetchType.LAZY: Admin details are loaded only when explicitly accessed.
     *
     * Note: This relationship will cause Hibernate to save the Admin when the User is saved,
     * provided the Admin object is correctly associated with the User object.
     */
    @OneToOne(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true, fetch = FetchType.LAZY)
    private Admin adminProfile; // Reference to the associated Admin profile

    /**
     * Justification: New @OneToOne mapping for Customer, enabling cascading persistence.
     * When a User is saved, its associated Customer profile will also be automatically persisted.
     * This follows the same robust pattern established for the Admin profile.
     */
    @OneToOne(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true, fetch = FetchType.LAZY)
    private Customer customerProfile; // <--- NEW MAPPING FOR CUSTOMER

}