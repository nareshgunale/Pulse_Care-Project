package com.ai_service.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PrescriptionDTO {
    private Long id;
    private Long patientId;
    private Long doctorId;
    private String doctorName;
    private Long appointmentId;
    private LocalDate prescriptionDate;
    private String prescriptionNotes;
    private List<MedicineDTO> medicines;
    private boolean archived;
    private String s3Key;
}