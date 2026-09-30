package com.hospital.healthpackage.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingRequest {

    @NotNull(message = "Package ID is required")
    private Long packageId;

    @NotBlank(message = "Patient name is required")
    @Size(min = 2, max = 100, message = "Name must be 2–100 characters")
    private String patientName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Enter a valid 10-digit Indian mobile number")
    private String phone;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    private String email;

    @NotNull(message = "Preferred date is required")
    @Future(message = "Booking date must be in the future")
    private LocalDate preferredDate;

    @NotBlank(message = "Time slot is required")
    private String timeSlot;

    private String notes;
}
