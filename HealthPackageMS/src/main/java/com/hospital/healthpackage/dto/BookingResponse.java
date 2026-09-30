package com.hospital.healthpackage.dto;

import com.hospital.healthpackage.entity.PackageBooking.BookingStatus;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {
    private Long id;
    private Long packageId;
    private String packageName;
    private Long userId;
    private String patientName;
    private String phone;
    private String email;
    private LocalDate preferredDate;
    private String timeSlot;
    private BookingStatus status;
    private String notes;
    private LocalDateTime bookedAt;
}
