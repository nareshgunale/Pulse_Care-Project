package com.ai_service.dto;

import lombok.Data;

/**
 * Used only for deserializing Feign responses — never persisted here.
 */
@Data
public class MedicineDTO {
    private Long id;
    private String medicineName;
    private Long medicineId;
    private String dosage;
    private String frequency;
    private Integer duration;
    private String routes;
    private String type;
    private String instructions;
    private Long prescriptionId;
}