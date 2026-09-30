package com.hms.appointment.entity;

import com.hms.appointment.dto.MedicineDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String medicineName;
    private Long medicineId;
    private String dosage;
    private String frequency;//eg-1-1-1 (morning-afternoon-evening) in a day
    private Integer duration;//eg-in days
    private String routes;//eg- oral, nasa madhe
    private String type;//eg-tablet,injection
    private String instructions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id")
    private Prescription prescription;

    public MedicineDTO toDTO() {
        return new MedicineDTO(id, medicineName, medicineId, dosage, frequency,
                duration, routes, type, instructions,
                prescription.getId());
    }


}
