package com.hms.appointment.entity;

import com.hms.appointment.dto.AppointmentDTO;
import com.hms.appointment.dto.AppointmentRecordDTO;
import com.hms.appointment.dto.RecordDetailsDTO;
import com.hms.appointment.utility.StringListConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AppointmentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long patientId;
    private Long doctorId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", unique = true, nullable = false)
    private Appointment appointment;
    private String symptoms;
    private String diagnosis;
    private String tests;
    private String notes;
    private String referrals;
    private LocalDate followUpDate;
    private LocalDateTime createdAt;

    public AppointmentRecordDTO toDTO() {
        return new AppointmentRecordDTO(id, patientId, doctorId, appointment.getId(),
                StringListConverter.convertStringToList(symptoms), diagnosis,
                StringListConverter.convertStringToList(tests),
                notes,referrals,null,followUpDate,createdAt);
    }

    public RecordDetailsDTO toRecordDetails() {
        return new RecordDetailsDTO(
                id,
                patientId,
                doctorId,
                null,
                appointment.getId(),
                StringListConverter.convertStringToList(symptoms),
                diagnosis,
                StringListConverter.convertStringToList(tests),
                notes,
                referrals,
                followUpDate,
                createdAt
        );
    }


}
