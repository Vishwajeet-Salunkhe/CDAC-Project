package com.example.online_car_service_station_backend.dto;

import com.example.online_car_service_station_backend.model.BookingStatus;
import com.example.online_car_service_station_backend.model.PaymentStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// Justification: This DTO is used by an administrator to update the status of a booking.
// It allows for partial updates (e.g., updating only the booking status) and prevents an admin
// from changing sensitive data like the booking ID or customer ID.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBookingRequest {
    private BookingStatus status;
    private PaymentStatus paymentStatus;
}