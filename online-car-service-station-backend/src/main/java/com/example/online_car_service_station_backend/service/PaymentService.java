package com.example.online_car_service_station_backend.service;

import com.example.online_car_service_station_backend.dto.PaymentConfirmationRequest;
import com.example.online_car_service_station_backend.dto.PaymentRequest;
import com.example.online_car_service_station_backend.dto.PaymentResponse;
import com.example.online_car_service_station_backend.model.Booking;
import com.example.online_car_service_station_backend.model.PaymentStatus;
import com.example.online_car_service_station_backend.repository.BookingRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import jakarta.transaction.Transactional;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class PaymentService {

    @Autowired
    private BookingRepository bookingRepository;

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    // Justification: This method creates a Razorpay order. It validates the booking in our database
    // and then calls the Razorpay API to generate a new order ID.
    public PaymentResponse createOrder(PaymentRequest paymentRequest) throws RazorpayException {
        Optional<Booking> bookingOptional = bookingRepository.findById(paymentRequest.getBookingId());
        if (bookingOptional.isEmpty()) {
            throw new RuntimeException("Booking not found.");
        }
        Booking booking = bookingOptional.get();

        if (booking.getPaymentStatus() == PaymentStatus.PAID) {
            throw new RuntimeException("Payment for this booking is already completed.");
        }

        RazorpayClient razorpayClient = new RazorpayClient(keyId, keySecret);
        JSONObject orderRequest = new JSONObject();

        // Justification: Razorpay expects the amount in the smallest currency unit (e.g., paise for INR).
        BigDecimal amountInPaise = paymentRequest.getAmount().multiply(new BigDecimal(100));
        orderRequest.put("amount", amountInPaise.intValue());
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "receipt_" + booking.getId());

        Order order = razorpayClient.orders.create(orderRequest);
        String orderId = order.get("id");

        System.out.println("Razorpay Order created for Booking " + booking.getId() + ": " + orderId);

        return new PaymentResponse(orderId, booking.getId(), paymentRequest.getAmount(), keyId);
    }

    // Justification: This method securely verifies the payment signature from the frontend.
    // This is a CRITICAL security step to prevent fraudulent payment confirmations.
    // The method uses the Razorpay utility to check if the signature matches the
    // combination of the payment ID, order ID, and your secret key.
    public boolean verifyPaymentSignature(PaymentConfirmationRequest confirmation) throws RazorpayException {
        String data = confirmation.getRazorpayOrderId() + "|" + confirmation.getRazorpayPaymentId();

        return Utils.verifySignature(data, confirmation.getRazorpaySignature(), keySecret);
    }

    // Justification: This method updates the booking status in our database.
    // It is marked with @Transactional to ensure that the status update is atomic.
    @Transactional
    public void updateBookingPaymentStatus(Long bookingId, PaymentStatus status) {
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
        if (bookingOptional.isPresent()) {
            Booking booking = bookingOptional.get();
            booking.setPaymentStatus(status);
            bookingRepository.save(booking);
            System.out.println("Booking " + bookingId + " payment status updated to: " + status);
        }
    }
}