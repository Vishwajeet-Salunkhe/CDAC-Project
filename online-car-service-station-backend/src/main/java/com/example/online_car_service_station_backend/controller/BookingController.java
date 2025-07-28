package com.example.online_car_service_station_backend.controller;

import com.example.online_car_service_station_backend.dto.*;
import com.example.online_car_service_station_backend.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody BookingRequest bookingRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        BookingResponse response = bookingService.createBooking(bookingRequest, userDetails);
        // Justification: This returns a single DTO object, not a List or an Entity.
        // It's the correct way to return a successful creation response.
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        // Justification: The service now returns a List of BookingResponse DTOs.
        List<BookingResponse> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/my-bookings")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<BookingResponse>> getCustomerBookings(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<BookingResponse> bookings = bookingService.getCustomerBookings(userDetails);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<BookingResponse> getBookingById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        Optional<BookingResponse> bookingResponse = bookingService.getBookingById(id, userDetails);
        return bookingResponse
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookingResponse> updateBookingStatus(
            @PathVariable Long id,
            @RequestBody UpdateBookingRequest updateRequest) {
        Optional<BookingResponse> updatedBooking = bookingService.updateBookingStatus(id, updateRequest);
        return updatedBooking
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Justification: This new endpoint allows an admin to delete a booking.
    // It is secured with @PreAuthorize to ensure only an admin can perform this action.
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        try {
            bookingService.deleteBooking(id);
            // Justification: Returns a 204 No Content response on successful deletion.
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Justification: This new endpoint is for the admin's stats page. It is secured with
    // @PreAuthorize to ensure only an admin can view this data.
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StatsResponse> getRevenueAndStats() {
        StatsResponse stats = bookingService.getRevenueAndStats();
        return ResponseEntity.ok(stats);
    }
    // Justification: CRITICAL FIX. This new endpoint allows a customer to submit feedback.
    // It is secured with @PreAuthorize to ensure only a customer can perform this action.
    @PostMapping("/feedback")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> submitFeedback(
            @Valid @RequestBody FeedbackRequest feedbackRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            bookingService.submitFeedback(feedbackRequest, userDetails);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }


}