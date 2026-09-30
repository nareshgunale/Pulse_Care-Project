package com.hospital.healthpackage.controller;

import com.hospital.healthpackage.dto.*;
import com.hospital.healthpackage.entity.PackageBooking.BookingStatus;
import com.hospital.healthpackage.service.PackageBookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
//@CrossOrigin(origins = "*")
public class BookingController {

    private final PackageBookingService bookingService;

    //Book a package
    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> bookPackage(@Valid @RequestBody BookingRequest request,
            Authentication authentication) {

        Long userId = (Long) authentication.getCredentials();
        BookingResponse booking = bookingService.bookPackage(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Package booked! We will confirm shortly.", booking));
    }

    //GET My bookings
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getMyBookings(Authentication authentication) {

        Long userId = (Long) authentication.getCredentials();
        return ResponseEntity.ok(ApiResponse.success("Your bookings", bookingService.getMyBookings(userId)));
    }

    //GET Single booking detail
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingById(@PathVariable Long id, Authentication authentication) {

        Long userId = (Long) authentication.getCredentials();
        return ResponseEntity.ok(ApiResponse.success("Booking detail",
                bookingService.getBookingById(id, userId)));
    }

    //PATCH Cancel booking
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<BookingResponse>> cancelBooking(@PathVariable Long id, Authentication authentication) {

        Long userId = (Long) authentication.getCredentials();
        return ResponseEntity.ok(ApiResponse.success("Booking cancelled",
                bookingService.cancelBooking(id, userId)));
    }

    //GET Admin: all bookings
    @GetMapping("/all")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getAllBookings() {
        return ResponseEntity.ok(ApiResponse.success("All bookings", bookingService.getAllBookings()));
    }

    //PATCH Admin: update status
    @PatchMapping("/{id}/status")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BookingResponse>> updateStatus(@PathVariable Long id, @RequestParam BookingStatus status) {
        return ResponseEntity.ok(ApiResponse.success("Status updated",
                bookingService.updateBookingStatus(id, status)));
    }
}
