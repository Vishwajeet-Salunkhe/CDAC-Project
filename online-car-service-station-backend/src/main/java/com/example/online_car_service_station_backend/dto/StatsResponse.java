package com.example.online_car_service_station_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

// Justification: This DTO is specifically for the admin's stats page. It encapsulates
// the total revenue from completed and paid bookings, providing a clean API contract.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatsResponse {
    private BigDecimal totalRevenue;
    private Long totalCompletedBookings;
}