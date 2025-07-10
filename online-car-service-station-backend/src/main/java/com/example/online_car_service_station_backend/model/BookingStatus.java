package com.example.online_car_service_station_backend.model;

// Justification: This enum defines the possible states of a booking, providing a clear
// and type-safe way to manage its lifecycle. Using an enum prevents using arbitrary strings,
// which reduces errors and improves code readability.
public enum BookingStatus {
    PENDING,        // Booking created, awaiting confirmation from admin
    CONFIRMED,      // Admin has confirmed the booking
    IN_PROGRESS,    // Service is currently being performed
    COMPLETED,      // Service has been completed
    CANCELLED       // Booking was cancelled by either customer or admin
}