package com.hms.pharmacy.dto;

import com.hms.pharmacy.constants.MedicineCategory;
import com.hms.pharmacy.constants.MedicineType;
import com.hms.pharmacy.entity.Medicine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicineDTO {

    private Long id;

    private String name;
    private String dosage;
    private MedicineCategory category;
    private MedicineType type;
    private String manufacturer;
    private Integer unitPrice;
    private Integer stock;
    private LocalDateTime createAt;

    public Medicine toEntity(){
        return new Medicine(id,name,dosage,category,type,manufacturer,unitPrice,stock, createAt);
    }
}
