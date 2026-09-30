package com.hospital.healthpackage.service;

import com.hospital.healthpackage.dto.BookingRequest;
import com.hospital.healthpackage.dto.BookingResponse;
import com.hospital.healthpackage.dto.HealthPackageRequest;
import com.hospital.healthpackage.dto.HealthPackageResponse;

import java.util.List;

public interface HealthPackageService {

    List<HealthPackageResponse> getAllPackages(String category, String gender);
    HealthPackageResponse getPackageById(Long id);
    List<HealthPackageResponse> getPopularPackages();
    List<String> getAllCategories();
    List<HealthPackageResponse> searchPackages(String query);
    HealthPackageResponse createPackage(HealthPackageRequest request);
    HealthPackageResponse updatePackage(Long id, HealthPackageRequest request);
    void deletePackage(Long id);
    BookingResponse bookPackage(BookingRequest request, Long userId);
    List<BookingResponse> getMyBookings(Long userId);
    BookingResponse cancelBooking(Long bookingId, Long userId);
}
