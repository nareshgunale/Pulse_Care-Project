package com.hospital.healthpackage.repository;

import com.hospital.healthpackage.entity.PackageBooking;
import com.hospital.healthpackage.entity.PackageBooking.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackageBookingRepository extends JpaRepository<PackageBooking, Long> {

    // All bookings by a specific user
    List<PackageBooking> findByUserIdOrderByBookedAtDesc(Long userId);

    // All bookings for a specific package
    List<PackageBooking> findByPackageId(Long packageId);

    // Bookings by status
    List<PackageBooking> findByStatus(BookingStatus status);

    // User bookings by status
    List<PackageBooking> findByUserIdAndStatus(Long userId, BookingStatus status);
}
