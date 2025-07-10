package com.example.online_car_service_station_backend.model;

// Justification: This enum defines the payment status for a booking. It provides
// a standardized way to track whether a payment is pending or has been successfully made.
public enum PaymentStatus {
    PENDING,        // Payment has not yet been received
    PAID            // Payment has been successfully processed
}