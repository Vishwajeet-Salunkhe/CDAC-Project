package com.example.online_car_service_station_backend.dto;

import lombok.Data;

// Justification: This DTO receives the payment confirmation details from the frontend.
// The signature is a critical security field used to verify the authenticity of the payment.
@Data
public class PaymentConfirmationRequest {
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;
    private Long bookingId;
}