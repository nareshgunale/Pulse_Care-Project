package com.hms.pharmacy.entity;

import com.hms.pharmacy.constants.StockStatus;
import com.hms.pharmacy.dto.MedicineInventoryDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class MedicineInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;
    private String batchNo;
    private Integer quantity;
    private LocalDate expireDate;
    private LocalDate addedDate;
    private Integer initialQuantity;
    private StockStatus stockStatus;

    public MedicineInventoryDTO toDto(){
        return new MedicineInventoryDTO(
                id,
                medicine != null ? medicine.getId() : null,
                batchNo,
                quantity,
                expireDate,
                addedDate,
                initialQuantity,
                stockStatus
        );
    }
}
