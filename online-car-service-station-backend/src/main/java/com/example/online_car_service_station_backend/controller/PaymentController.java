package com.example.online_car_service_station_backend.controller;

import com.example.online_car_service_station_backend.dto.PaymentConfirmationRequest;
import com.example.online_car_service_station_backend.dto.PaymentRequest;
import com.example.online_car_service_station_backend.dto.PaymentResponse;
import com.example.online_car_service_station_backend.model.PaymentStatus;
import com.example.online_car_service_station_backend.service.PaymentService;
import com.razorpay.RazorpayException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // Justification: This endpoint is called by the frontend to create a Razorpay order.
    // It's secured for customers. It returns the Order ID and other details needed by the frontend
    // to open the Razorpay payment form.
    @PostMapping("/create-order")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> createRazorpayOrder(@Valid @RequestBody PaymentRequest paymentRequest) {
        try {
            PaymentResponse response = paymentService.createOrder(paymentRequest);
            return ResponseEntity.ok(response);
        } catch (RazorpayException | RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Justification: This endpoint receives the payment confirmation details from the frontend
    // after a successful payment. It is responsible for verifying the signature to prevent fraud
    // and then updating the booking's payment status in our database.
    @PostMapping("/verify-payment")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> verifyPayment(@Valid @RequestBody PaymentConfirmationRequest confirmation) {
        try {
            boolean isVerified = paymentService.verifyPaymentSignature(confirmation);

            if (isVerified) {
                paymentService.updateBookingPaymentStatus(confirmation.getBookingId(), PaymentStatus.PAID);
                return ResponseEntity.ok("Payment confirmed successfully.");
            } else {
                return ResponseEntity.badRequest().body("Payment verification failed.");
            }
        } catch (RazorpayException e) {
            return ResponseEntity.badRequest().body("Razorpay error: " + e.getMessage());
        }
    }
}