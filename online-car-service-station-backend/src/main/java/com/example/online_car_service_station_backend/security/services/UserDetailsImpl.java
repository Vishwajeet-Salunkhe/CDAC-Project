package com.example.online_car_service_station_backend.security.services;

import com.example.online_car_service_station_backend.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

// Justification: This class is a custom implementation of Spring Security's UserDetails interface.
// It acts as an adapter, translating our application's User entity into a format that Spring Security
// can understand for authentication and authorization. It holds essential user details and their authorities (roles).
@Data // Lombok for getters, setters, equals, hashCode, toString
// Justification: Exclude 'user' from equals/hashCode calculation to prevent potential recursion or
// issues with JPA proxy objects if 'user' is eagerly loaded or part of the identity comparison.
// We primarily use the ID for identity check.
@EqualsAndHashCode(exclude = "user")
public class UserDetailsImpl implements UserDetails {

    private static final long serialVersionUID = 1L; // For serialization

    private Long id;
    private String username;
    private String email;

    // Justification: @JsonIgnore prevents the password from being serialized into the JWT or
    // sent in any JSON responses, enhancing security.
    @JsonIgnore
    private String password;

    // Justification: Stores the user's authorities (roles) as a collection of GrantedAuthority objects.
    // SimpleGrantedAuthority is a concrete implementation suitable for simple role names.
    private Collection<? extends GrantedAuthority> authorities;

    // Justification: Private constructor to enforce creation via the build method, ensuring
    // proper initialization of authorities from the User entity's roles.
    private UserDetailsImpl(Long id, String username, String email, String password,
                            Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    // Justification: Static factory method to create a UserDetailsImpl instance from our User entity.
    // It maps the User's roles (Set<Role>) into Spring Security's GrantedAuthority collection.
    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name())) // Convert ERole to String name
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                authorities);
    }

    // Justification: These methods are part of the UserDetails contract.
    // They define account status properties. For simplicity, we set them to true,
    // assuming accounts are always enabled, not expired, locked, or credentials expired.
    // In a real application, these might be dynamically determined based on user status fields.
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // Justification: Overriding equals and hashCode is crucial for comparing UserDetailsImpl objects.
    // We compare based on the user's ID, as it's the unique identifier.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}