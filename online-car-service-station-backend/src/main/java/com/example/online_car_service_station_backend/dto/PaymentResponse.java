package com.example.online_car_service_station_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// Justification: This DTO sends the necessary information back to the frontend
// to open the Razorpay payment form.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private String orderId;
    private Long bookingId;
    private BigDecimal amount;
    private String keyId; // Your Razorpay key ID
}