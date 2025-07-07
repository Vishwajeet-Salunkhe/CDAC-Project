package com.example.online_car_service_station_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

// Justification: The Booking entity is a core part of the application, representing a service appointment.
// It tracks key information like the customer, booking date, status, and associated services.
@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor

// Justification: CRITICAL FIX. We must exclude the 'bookingServices' collection from
// the equals() and hashCode() methods to prevent infinite recursion, which would
// cause a StackOverflowError. The other side of this relationship (BookedService)
// will be configured to exclude the 'booking' field.
@EqualsAndHashCode(exclude = "bookingServices")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Justification: Establishes a Many-to-One relationship with the Customer.
    // A single customer can have many bookings, but each booking belongs to only one customer.
    // The @JoinColumn specifies the foreign key column.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonIgnore // Justification: Prevents the entire Customer object from being serialized
    private Customer customer;

    // Justification: A booking can be made for multiple services, and a service can be part of many bookings.
    // This forms a Many-to-Many relationship.
    // We use a dedicated entity, BookingService, to represent the join table, which allows us
    // to store extra information like the price at the time of booking.
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<BookedService> bookingServices = new HashSet<>();

    // Justification: The requested date and time for the service.
    @Column(nullable = false)
    private LocalDateTime bookingDateTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    // Justification: The total amount for all services in this booking.
    // Using BigDecimal for monetary values to avoid precision issues.
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;


    // Justification: These new fields store the customer's feedback. They are nullable
    // because a booking may not have feedback.
    private Integer rating;
    @Lob
    private String comment;

}