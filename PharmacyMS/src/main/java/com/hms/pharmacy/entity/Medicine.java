package com.hms.pharmacy.entity;

import com.hms.pharmacy.constants.MedicineCategory;
import com.hms.pharmacy.constants.MedicineType;
import com.hms.pharmacy.dto.MedicineDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String dosage;
    private MedicineCategory category;
    private MedicineType type;
    private String manufacturer;
    private Integer unitPrice;
    private Integer stock;
    private LocalDateTime createAt;

    public Medicine(Long id){
        this.id= id;
    }

    public MedicineDTO toDto(){
        return new MedicineDTO(id,name,dosage,category,type,manufacturer,unitPrice,stock,createAt);
    }
}
