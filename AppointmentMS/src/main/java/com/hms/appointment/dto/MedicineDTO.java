package com.hms.appointment.dto;

import com.hms.appointment.entity.Medicine;
import com.hms.appointment.entity.Prescription;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicineDTO {
    private Long id;
    private String medicineName;
    private Long medicineId;
    private String dosage;
    private String frequency;//eg-1-1-1 (morning-afternoon-evening) in a day
    private Integer duration;//eg-in days
    private String routes;//eg- oral, nasa madhe
    private String type;//eg-tablet,injection
    private String instructions;
    private Long prescriptionId;

    public Medicine toEntity() {
        return new Medicine(
                id,
                medicineName,
                medicineId,
                dosage,
                frequency,
                duration,
                routes,
                type,
                instructions,
                new Prescription(prescriptionId)
        );
    }
}
