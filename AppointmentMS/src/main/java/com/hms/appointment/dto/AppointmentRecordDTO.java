package com.hms.appointment.dto;

import com.hms.appointment.entity.Appointment;
import com.hms.appointment.entity.AppointmentRecord;
import com.hms.appointment.utility.StringListConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentRecordDTO {

    private Long id;
    private Long patientId;
    private Long doctorId;
    private Long appointmentId;
    private List<String> symptoms;
    private String diagnosis;
    private List<String> tests;
    private String notes;
    private String referrals;
    private PrescriptionDTO prescription;
    private LocalDate followUpDate;
    private LocalDateTime createdAt;

    public AppointmentRecord toEntity() {
        return new AppointmentRecord(id, patientId, doctorId,
                new Appointment(appointmentId),
                StringListConverter.convertListToString(symptoms),
                diagnosis,
                StringListConverter.convertListToString(tests),
                notes, referrals, followUpDate, createdAt);
    }
}
