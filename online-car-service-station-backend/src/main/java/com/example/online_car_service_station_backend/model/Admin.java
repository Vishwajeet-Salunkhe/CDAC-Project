package com.example.online_car_service_station_backend.model;

import jakarta.persistence.*; // JPA annotations for entity mapping
import lombok.Data; // Lombok for getters, setters, toString, equals, hashCode
import lombok.NoArgsConstructor; // Lombok for no-arg constructor
import lombok.AllArgsConstructor; // Lombok for all-args constructor
import lombok.EqualsAndHashCode; // Lombok for custom equals/hashCode generation (Crucial addition)

/**
 * Justification: The Admin entity represents the specific profile details for an administrator.
 * It does not hold authentication-related data (like username, password, roles), as those are managed
 * by the base {@link User} entity. Instead, it extends the base User's identity (shared primary key)
 * to provide administrator-specific attributes, following a common pattern for extending user types.
 */
@Entity // Marks this class as a JPA entity, meaning it maps to a database table.
@Table(name = "admins") // Specifies the name of the database table for this entity.
@Data // Lombok annotation to automatically generate getters, setters, toString(), equals(), and hashCode().
@NoArgsConstructor // Lombok annotation to generate a no-argument constructor (required by JPA).
@AllArgsConstructor // Lombok annotation to generate a constructor with all fields as arguments.
// Justification: CRITICAL FIX. We explicitly exclude the 'user' field from equals() and hashCode()
// generation for the same reason as with the Customer entity, preventing infinite recursion.
@EqualsAndHashCode(callSuper = false, exclude = "user")

/**
 * Justification for @EqualsAndHashCode(callSuper = false):
 *
 * 1.  Shared Primary Key (One-to-One with @MapsId): In this setup, the 'Admin' entity does not have its
 * own auto-generated primary key; instead, its 'id' is derived directly from the 'User' entity's 'id'.
 * This means Admin's 'id' is effectively its primary key AND its foreign key to the User table.
 *
 * 2.  Hibernate's Identity Management: Hibernate relies heavily on `equals()` and `hashCode()` methods
 * to manage entity identities within its persistence context (e.g., to determine if two entities
 * are the same object, or if an entity needs to be re-persisted/merged).
 *
 * 3.  Default Lombok Behavior: By default, `@Data` (which includes `@EqualsAndHashCode`) generates `equals()`
 * and `hashCode()` based on all non-static, non-transient fields. If this entity were part of a true
 * inheritance hierarchy (where Admin `extends` User in Java), Lombok's `@EqualsAndHashCode` might, by default,
 * try to call the superclass's `equals()` and `hashCode()` methods (`callSuper = true`).
 *
 * 4.  The Problem (without `callSuper = false` in this specific setup):
 * Even though 'Admin' doesn't *extend* 'User' in Java (it uses composition with `@OneToOne`), in complex JPA
 * relationships, especially with shared primary keys, there can sometimes be subtle issues if Hibernate's
 * internal identity checks (which rely on `equals` and `hashCode`) get confused.
 * When `id` is the only property determining equality (as is the case with a primary key mapped by `@MapsId`),
 * and the `@OneToOne` `user` field is also part of the `equals/hashCode` calculation, it can lead to circular
 * dependencies or unexpected behavior in the persistence context during initial entity persistence.
 * If `equals()` or `hashCode()` involves the `user` field *before* the `user` entity itself is fully managed
 * or flushed by Hibernate in a specific way, it can lead to state inconsistencies or `AssertionFailure`s.
 *
 * 5.  The Solution: By explicitly setting `callSuper = false`, we instruct Lombok to generate `equals()` and `hashCode()`
 * *only* based on the fields defined within the `Admin` class itself (primarily `id` and potentially `firstName`, `lastName`).
 * This is crucial here because the `id` is the unique identifier, and we want `equals()` and `hashCode()` to solely
 * rely on that `id` to prevent any unexpected interactions with the `user` reference *during the very first persist
 * when the relationship is being established*. It simplifies identity comparison for Hibernate in this specific scenario.
 * It helps ensure that Hibernate correctly identifies the Admin entity based on its primary key, which is mapped
 * via `@MapsId` from the User entity, avoiding internal assertion failures or persistence issues.
 */
public class Admin {

    /**
     * Justification: The 'id' field serves as the primary key for the 'admins' table.
     * In this specific setup, it's a shared primary key with the {@link User} entity,
     * meaning an Admin's ID is the same as its corresponding User's ID.
     * It does NOT use `@GeneratedValue` here, as its value is copied from the User entity.
     */
    @Id // Declares this field as the primary key of the entity.
    private Long id;

    // Justification: Standard fields for an administrator's personal details.
    private String firstName;
    private String lastName;
    // Add any other admin-specific fields if necessary, e.g., 'department', 'employeeId'

    // Justification: CRITICAL FIX. Added the profileImageUrl field to the Admin entity.
    // This allows the backend to store the image URL uploaded by an admin, making
    // the feature complete and consistent across all user types.
    private String profileImageUrl;


    /**
     * Justification: Defines the One-to-One relationship between the Admin entity and the User entity.
     * - @OneToOne: Indicates that one Admin record corresponds to exactly one User record.
     * - fetch = FetchType.LAZY: Specifies that the associated User entity should be loaded from the database
     * only when it is explicitly accessed (e.g., calling `admin.getUser()`). This is generally
     * recommended for performance to avoid loading unnecessary data.
     * - @MapsId: This is a crucial annotation in this context. It indicates that the primary key of
     * the Admin entity (this 'id' field) is also a foreign key to the User entity. Hibernate will
     * automatically manage copying the ID from the associated User object to this Admin's ID field
     * when persisting.
     * - @JoinColumn(name = "id"): Specifies the foreign key column in the 'admins' table that references
     * the primary key column ('id') in the 'users' table. The 'name = "id"' signifies that the foreign
     * key column in the 'admins' table is also named 'id'.
     *
     * This setup creates a shared primary key relationship: the 'id' column in 'admins' serves both as
     * its own primary key and as the foreign key linking it to the 'users' table.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // Ensures the primary key of Admin is mapped from the primary key of User
    @JoinColumn(name = "id") // The foreign key column in the 'admins' table which references 'users.id'
    private User user; // Reference to the associated User entity (the "parent" entity in this relationship)
}