package com.hms.appointment.dto;

import com.hms.appointment.entity.Appointment;
import com.hms.appointment.entity.Prescription;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionDTO {

    private Long id;
    private Long patientId;
    private Long doctorId;
    private String doctorName;
    private Long appointmentId;
    private LocalDate prescriptionDate;
    private String prescriptionNotes;
    private List<MedicineDTO> medicines;
    private Boolean archived;
    private String s3Key;

    public Prescription toEntity() {
        return new Prescription(
                id, patientId, doctorId, doctorName,
                new Appointment(appointmentId),
                prescriptionDate, prescriptionNotes,
                archived != null ? archived : false,
                s3Key,new ArrayList<>()
        );
    }
}
