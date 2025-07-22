package com.example.online_car_service_station_backend.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

// Justification: This DTO encapsulates the data a customer provides when creating a new booking.
// Using a DTO for input ensures that the API only accepts the necessary fields for this specific
// operation and prevents clients from sending extraneous or unauthorized data.
@Data
public class BookingRequest {

    // Justification: A booking must have at least one service. @NotEmpty ensures the list is not empty.
    @NotEmpty(message = "Service IDs cannot be empty")
    private List<Long> carServiceIds;

    // Justification: The booking date and time must be specified.
    // @NotNull ensures the field is not null. @FutureOrPresent ensures the date is not in the past.
    // @DateTimeFormat helps with parsing the date string from the request body.
    @NotNull(message = "Booking date and time must be provided")
    @FutureOrPresent(message = "Booking date and time must be in the present or future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime bookingDateTime;
}