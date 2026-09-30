package com.hospital.healthpackage.service.serviceImp;

import com.hospital.healthpackage.dto.BookingRequest;
import com.hospital.healthpackage.dto.BookingResponse;
import com.hospital.healthpackage.entity.HealthPackage;
import com.hospital.healthpackage.entity.PackageBooking;
import com.hospital.healthpackage.entity.PackageBooking.BookingStatus;
import com.hospital.healthpackage.exception.ResourceNotFoundException;
import com.hospital.healthpackage.repository.HealthPackageRepository;
import com.hospital.healthpackage.repository.PackageBookingRepository;
import com.hospital.healthpackage.service.PackageBookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PackageBookingServiceImp implements PackageBookingService {

    private final PackageBookingRepository bookingRepository;
    private final HealthPackageRepository packageRepository;


    @Transactional
    @Override
    public BookingResponse bookPackage(BookingRequest request, Long userId) {

        HealthPackage pkg = packageRepository.findById(request.getPackageId())
                .filter(HealthPackage::getIsActive)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Active package not found with id: " + request.getPackageId()));

        PackageBooking booking = PackageBooking.builder()
                .packageId(request.getPackageId())
                .userId(userId)
                .patientName(request.getPatientName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .preferredDate(request.getPreferredDate())
                .timeSlot(request.getTimeSlot())
                .notes(request.getNotes())
                .status(BookingStatus.PENDING)
                .build();

        PackageBooking saved = bookingRepository.save(booking);
        log.info("New booking created: bookingId={}, packageId={}, userId={}",
                saved.getId(), pkg.getId(), userId);

        return toResponse(saved, pkg.getName());
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingResponse> getMyBookings(Long userId) {
        return bookingRepository.findByUserIdOrderByBookedAtDesc(userId)
                .stream()
                .map(b -> {
                    String pkgName = packageRepository.findById(b.getPackageId())
                            .map(HealthPackage::getName)
                            .orElse("Package Unavailable");
                    return toResponse(b, pkgName);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public BookingResponse getBookingById(Long bookingId, Long userId) {
        PackageBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Booking not found with id: " + bookingId));

        if (!booking.getUserId().equals(userId)) {
            throw new RuntimeException("You are not authorized to view this booking");
        }

        String pkgName = packageRepository.findById(booking.getPackageId())
                .map(HealthPackage::getName).orElse("Package Unavailable");

        return toResponse(booking, pkgName);
    }


    @Transactional
    @Override
    public BookingResponse cancelBooking(Long bookingId, Long userId) {
        PackageBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Booking not found with id: " + bookingId));

        if (!booking.getUserId().equals(userId)) {
            throw new RuntimeException("You are not authorized to cancel this booking");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new RuntimeException("Booking is already cancelled");
        }

        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new RuntimeException("Completed booking cannot be cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        PackageBooking saved = bookingRepository.save(booking);
        log.info("Booking cancelled: bookingId={}, userId={}", bookingId, userId);

        String pkgName = packageRepository.findById(saved.getPackageId())
                .map(HealthPackage::getName).orElse("Package Unavailable");

        return toResponse(saved, pkgName);
    }


    @Transactional(readOnly = true)
    @Override
    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll()
                .stream()
                .map(b -> {
                    String pkgName = packageRepository.findById(b.getPackageId())
                            .map(HealthPackage::getName).orElse("Package Unavailable");
                    return toResponse(b, pkgName);
                })
                .collect(Collectors.toList());
    }


    @Transactional
    @Override
    public BookingResponse updateBookingStatus(Long bookingId, BookingStatus newStatus) {
        PackageBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Booking not found with id: " + bookingId));

        booking.setStatus(newStatus);
        PackageBooking saved = bookingRepository.save(booking);
        log.info("Booking status updated: bookingId={}, newStatus={}", bookingId, newStatus);

        String pkgName = packageRepository.findById(saved.getPackageId())
                .map(HealthPackage::getName).orElse("Package Unavailable");

        return toResponse(saved, pkgName);
    }



    private BookingResponse toResponse(PackageBooking booking, String packageName) {
        return BookingResponse.builder()
                .id(booking.getId())
                .packageId(booking.getPackageId())
                .packageName(packageName)
                .userId(booking.getUserId())
                .patientName(booking.getPatientName())
                .phone(booking.getPhone())
                .email(booking.getEmail())
                .preferredDate(booking.getPreferredDate())
                .timeSlot(booking.getTimeSlot())
                .status(booking.getStatus())
                .notes(booking.getNotes())
                .bookedAt(booking.getBookedAt())
                .build();
    }
}
