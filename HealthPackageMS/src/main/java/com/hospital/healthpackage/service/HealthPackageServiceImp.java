package com.hospital.healthpackage.service;

import com.hospital.healthpackage.dto.*;
import com.hospital.healthpackage.entity.HealthPackage;
import com.hospital.healthpackage.entity.PackageBooking;
import com.hospital.healthpackage.exception.ResourceNotFoundException;
import com.hospital.healthpackage.repository.HealthPackageRepository;
import com.hospital.healthpackage.repository.PackageBookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HealthPackageServiceImp implements HealthPackageService {

    private final HealthPackageRepository packageRepository;
    private final PackageBookingRepository bookingRepository;

    @Override
    public List<HealthPackageResponse> getAllPackages(String category, String gender) {
        List<HealthPackage> packages;

        List<String> genderOptions = gender != null
                ? List.of(gender, "Both")
                : List.of("Male", "Female", "Both");

        if (category != null && !category.isBlank()) {
            packages = packageRepository
                    .findByCategoryIgnoreCaseAndIdealForInAndIsActiveTrue(category, genderOptions);
        } else if (gender != null) {
            packages = packageRepository.findByIdealForInAndIsActiveTrue(genderOptions);
        } else {
            packages = packageRepository.findByIsActiveTrue();
        }

        return packages.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public HealthPackageResponse getPackageById(Long id) {
        HealthPackage pkg = packageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Package not found with id: " + id));
        return toResponse(pkg);
    }

    @Override
    public List<HealthPackageResponse> getPopularPackages() {
        return packageRepository.findByIsPopularTrueAndIsActiveTrue()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<String> getAllCategories() {
        return packageRepository.findAllActiveCategories();
    }

    @Override
    public List<HealthPackageResponse> searchPackages(String query) {
        return packageRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(query)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public HealthPackageResponse createPackage(HealthPackageRequest request) {
        HealthPackage pkg = HealthPackage.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .category(request.getCategory())
                .idealFor(request.getIdealFor())
                .ageGroup(request.getAgeGroup())
                .parameters(request.getParameters())
                .tests(request.getTests())
                .imageUrl(request.getImageUrl())
                .isPopular(request.getIsPopular() != null && request.getIsPopular())
                .isActive(true)
                .build();
        return toResponse(packageRepository.save(pkg));
    }

    @Transactional
    @Override
    public HealthPackageResponse updatePackage(Long id, HealthPackageRequest request) {
        HealthPackage pkg = packageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Package not found with id: " + id));

        pkg.setName(request.getName());
        pkg.setDescription(request.getDescription());
        pkg.setPrice(request.getPrice());
        pkg.setCategory(request.getCategory());
        pkg.setIdealFor(request.getIdealFor());
        pkg.setAgeGroup(request.getAgeGroup());
        pkg.setParameters(request.getParameters());
        pkg.setTests(request.getTests());
        pkg.setImageUrl(request.getImageUrl());
        if (request.getIsPopular() != null) pkg.setIsPopular(request.getIsPopular());

        return toResponse(packageRepository.save(pkg));
    }

    @Transactional
    @Override
    public void deletePackage(Long id) {
        HealthPackage pkg = packageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Package not found with id: " + id));
        pkg.setIsActive(false); // Soft delete
        packageRepository.save(pkg);
    }


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
                .status(PackageBooking.BookingStatus.PENDING)
                .build();

        PackageBooking saved = bookingRepository.save(booking);
        log.info("Package booked: packageId={}, userId={}, bookingId={}", 
                pkg.getId(), userId, saved.getId());

        return toBookingResponse(saved, pkg.getName());
    }

    @Override
    public List<BookingResponse> getMyBookings(Long userId) {
        return bookingRepository.findByUserIdOrderByBookedAtDesc(userId)
                .stream()
                .map(b -> {
                    String pkgName = packageRepository.findById(b.getPackageId())
                            .map(HealthPackage::getName).orElse("Unknown Package");
                    return toBookingResponse(b, pkgName);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public BookingResponse cancelBooking(Long bookingId, Long userId) {
        PackageBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        if (!booking.getUserId().equals(userId)) {
            throw new RuntimeException("You are not authorized to cancel this booking");
        }
        if (booking.getStatus() == PackageBooking.BookingStatus.CANCELLED) {
            throw new RuntimeException("Booking is already cancelled");
        }

        booking.setStatus(PackageBooking.BookingStatus.CANCELLED);
        PackageBooking saved = bookingRepository.save(booking);

        String pkgName = packageRepository.findById(saved.getPackageId())
                .map(HealthPackage::getName).orElse("Unknown");
        return toBookingResponse(saved, pkgName);
    }


    private HealthPackageResponse toResponse(HealthPackage pkg) {
        return HealthPackageResponse.builder()
                .id(pkg.getId())
                .name(pkg.getName())
                .description(pkg.getDescription())
                .price(pkg.getPrice())
                .category(pkg.getCategory())
                .idealFor(pkg.getIdealFor())
                .ageGroup(pkg.getAgeGroup())
                .parameters(pkg.getParameters())
                .tests(pkg.getTests())
                .imageUrl(pkg.getImageUrl())
                .isActive(pkg.getIsActive())
                .isPopular(pkg.getIsPopular())
                .createdAt(pkg.getCreatedAt())
                .build();
    }

    private BookingResponse toBookingResponse(PackageBooking booking, String packageName) {
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
