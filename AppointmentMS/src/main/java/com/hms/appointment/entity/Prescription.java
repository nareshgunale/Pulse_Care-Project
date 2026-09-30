package com.hms.appointment.entity;

import com.hms.appointment.dto.PrescriptionDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long patientId;
    private Long doctorId;
    private String doctorName;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;
    private LocalDate prescriptionDate;
    private String prescriptionNotes;

    @Column(name = "is_archived", nullable = false)
    private boolean archived = false;

    @Column(name = "s3_key")
    private String s3Key;

    @OneToMany(mappedBy = "prescription", fetch = FetchType.EAGER)
    private List<Medicine> medicines = new ArrayList<>();


    public Prescription(Long id) {
        this.id=id;
    }

    public PrescriptionDTO toDTO() {
        return new PrescriptionDTO(
                id, patientId, doctorId, doctorName,
                appointment.getId(),
                prescriptionDate,
                prescriptionNotes,
                medicines.stream()
                        .map(Medicine::toDTO)
                        .collect(Collectors.toList()),
                archived,
                s3Key
        );
    }
}
