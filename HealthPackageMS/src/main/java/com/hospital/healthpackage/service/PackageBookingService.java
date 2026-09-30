package com.hospital.healthpackage.service;

import com.hospital.healthpackage.dto.BookingRequest;
import com.hospital.healthpackage.dto.BookingResponse;
import com.hospital.healthpackage.entity.PackageBooking;

import java.util.List;

public interface PackageBookingService {

    BookingResponse bookPackage(BookingRequest request, Long userId);

    List<BookingResponse> getMyBookings(Long userId);

    BookingResponse getBookingById(Long bookingId, Long userId);

    BookingResponse cancelBooking(Long bookingId, Long userId);

    List<BookingResponse> getAllBookings();

    BookingResponse updateBookingStatus(Long bookingId, PackageBooking.BookingStatus newStatus);
}
