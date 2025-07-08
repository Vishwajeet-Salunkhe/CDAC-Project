package com.example.online_car_service_station_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

// Justification: This entity models the many-to-many relationship between Booking and CarService.
// It's a best practice to use a separate entity for join tables when you need to store
// additional data on the relationship itself, such as the price of the service at the time of booking.
@Entity
@Table(name = "booking_services")
@Data
@NoArgsConstructor
@AllArgsConstructor
// Justification: CRITICAL FIX. We must exclude the 'booking' field from the equals() and
// hashCode() methods to prevent infinite recursion with the Booking entity. This works
// in tandem with the exclusion on the Booking side to correctly break the circular dependency.
@EqualsAndHashCode(exclude = "booking")
public class BookedService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    @JsonIgnore // Justification: Prevents the Booking entity from being serialized
    private Booking booking;

    @ManyToOne(fetch = FetchType.EAGER) // Fetch service details immediately
    @JoinColumn(name = "car_service_id", nullable = false)
    private CarService carService;

    // Justification: Stores the price of the service at the time of booking.
    // This is crucial because the service's price can change over time, and we need a historical record
    // of the price that was agreed upon for this specific booking.
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal priceAtBooking;

    // Justification: Stores the quantity of this service if multiple are booked (e.g., two tire rotations).
    @Column(nullable = false)
    private Integer quantity;
}