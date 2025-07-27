package com.example.online_car_service_station_backend.service;

import com.example.online_car_service_station_backend.dto.*;
import com.example.online_car_service_station_backend.model.*;
import com.example.online_car_service_station_backend.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingServiceRepository bookingServiceRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ServiceRepository carServiceRepository;

    @Autowired
    private UserRepository userRepository;

    private BookingResponse mapToResponse(Booking booking) {
        List<ServiceResponse> bookedServices = booking.getBookingServices().stream()
                .map(bookedService -> new ServiceResponse(
                        bookedService.getCarService().getId(),
                        bookedService.getCarService().getName(),
                        bookedService.getCarService().getDescription(),
                        bookedService.getPriceAtBooking(),
                        bookedService.getCarService().getImageUrl()))
                .collect(Collectors.toList());

        return new BookingResponse(
                booking.getId(),
                booking.getCustomer().getId(),
                booking.getCustomer().getUser().getUsername(),
                booking.getCustomer().getFirstName() + " " + booking.getCustomer().getLastName(),
                booking.getBookingDateTime(),
                booking.getStatus(),
                booking.getPaymentStatus(),
                booking.getTotalAmount(),
                bookedServices,
                // CRITICAL FIX: The rating and comment fields are now included in the DTO.
                booking.getRating(),
                booking.getComment()
        );
    }

    @Transactional
    public BookingResponse createBooking(BookingRequest bookingRequest, UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found."));
        Customer customer = customerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Customer profile not found for user."));

        List<CarService> requestedServices = carServiceRepository.findAllById(bookingRequest.getCarServiceIds());
        if (requestedServices.size() != bookingRequest.getCarServiceIds().size()) {
            throw new RuntimeException("One or more service IDs are invalid.");
        }

        BigDecimal totalAmount = requestedServices.stream()
                .map(CarService::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setBookingDateTime(bookingRequest.getBookingDateTime());
        booking.setStatus(BookingStatus.PENDING);
        booking.setPaymentStatus(PaymentStatus.PENDING);
        booking.setTotalAmount(totalAmount);
        Booking savedBooking = bookingRepository.save(booking);

        for (CarService carService : requestedServices) {
            BookedService bookedServiceEntity = new BookedService();
            bookedServiceEntity.setBooking(savedBooking);
            bookedServiceEntity.setCarService(carService);
            bookedServiceEntity.setPriceAtBooking(carService.getPrice());
            bookedServiceEntity.setQuantity(1);
            bookingServiceRepository.save(bookedServiceEntity);
            savedBooking.getBookingServices().add(bookedServiceEntity);
        }

        return mapToResponse(savedBooking);
    }

    @Transactional
    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<BookingResponse> getCustomerBookings(UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found."));
        Customer customer = customerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Customer profile not found for user."));
        return bookingRepository.findByCustomer(customer).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public Optional<BookingResponse> getBookingById(Long id, UserDetails userDetails) {
        return bookingRepository.findById(id)
                .flatMap(booking -> {
                    User user = userRepository.findByUsername(userDetails.getUsername())
                            .orElseThrow(() -> new RuntimeException("Authenticated user not found."));
                    if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                        return Optional.of(mapToResponse(booking));
                    }
                    if (booking.getCustomer().getUser().getId().equals(user.getId())) {
                        return Optional.of(mapToResponse(booking));
                    }
                    return Optional.empty();
                });
    }

    @Transactional
    public Optional<BookingResponse> updateBookingStatus(Long id, UpdateBookingRequest updateRequest) {
        return bookingRepository.findById(id).map(booking -> {
            if (updateRequest.getStatus() != null) {
                booking.setStatus(updateRequest.getStatus());
            }
            if (updateRequest.getPaymentStatus() != null) {
                booking.setPaymentStatus(updateRequest.getPaymentStatus());
            }
            Booking updatedBooking = bookingRepository.save(booking);
            return mapToResponse(updatedBooking);
        });
    }

    // Justification: This method has been added to handle deleting a booking.
    // It is called from the BookingController's DELETE endpoint.
    @Transactional
    public void deleteBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));
        if (booking.getStatus() != BookingStatus.COMPLETED) {
            throw new RuntimeException("Booking can only be deleted if its status is 'COMPLETED'.");
        }
        bookingRepository.delete(booking);
        System.out.println("Booking " + bookingId + " has been deleted.");
    }

    // Justification: This method has been added to calculate revenue and stats.
    // It is called from the BookingController's GET /stats endpoint.
    @Transactional
    public StatsResponse getRevenueAndStats() {
        List<Booking> completedPaidBookings = bookingRepository.findAll().stream()
                .filter(b -> b.getStatus() == BookingStatus.COMPLETED && b.getPaymentStatus() == PaymentStatus.PAID)
                .collect(Collectors.toList());

        BigDecimal totalRevenue = completedPaidBookings.stream()
                .map(Booking::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Long totalCount = (long) completedPaidBookings.size();

        return new StatsResponse(totalRevenue, totalCount);
    }

    @Transactional
    public void submitFeedback(FeedbackRequest feedbackRequest, UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found."));

        Booking booking = bookingRepository.findById(feedbackRequest.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found."));

        if (booking.getCustomer().getUser().getId() != user.getId()) {
            throw new RuntimeException("You can only leave feedback for your own bookings.");
        }

        if (booking.getStatus() != BookingStatus.COMPLETED) {
            throw new RuntimeException("You can only leave feedback for completed bookings.");
        }

        if (booking.getRating() != null) {
            throw new RuntimeException("Feedback for this booking has already been submitted.");
        }

        booking.setRating(feedbackRequest.getRating());
        booking.setComment(feedbackRequest.getComment());
        bookingRepository.save(booking);
    }
}