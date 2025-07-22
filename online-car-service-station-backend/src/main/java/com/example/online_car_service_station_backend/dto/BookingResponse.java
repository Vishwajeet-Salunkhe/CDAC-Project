package com.example.online_car_service_station_backend.dto;

import com.example.online_car_service_station_backend.model.Booking;
import com.example.online_car_service_station_backend.model.BookingStatus;
import com.example.online_car_service_station_backend.model.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// Justification: This DTO defines the structure of the data returned to the client for a booking.
// It's a flattened representation of the Booking entity and its related data, making it easier
// for the frontend to consume. This DTO hides JPA-specific objects and controls what information is exposed.
// Justification: This is a CRITICAL FIX for the serialization issue.
// By adding this annotation, we instruct the JSON serializer to exclude any fields
// from the output if their value is null. This will prevent the 'id', 'customer',
// and 'bookingServices' fields from the underlying Booking entity from appearing
// in the final API response, resulting in a clean DTO-only output.
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingResponse {
    private Long bookingId;
    private Long customerId;
    private String customerUsername;
    private String customerFullName;
    private LocalDateTime bookingDateTime;
    private BookingStatus status;
    private PaymentStatus paymentStatus;
    private BigDecimal totalAmount;
    private List<ServiceResponse> bookedServices; // A list of the services in this booking.

    // Justification: CRITICAL FIX. These new fields are added to the DTO
    // to allow the frontend to display the rating and comment for a booking.
    private Integer rating;
    private String comment;
}