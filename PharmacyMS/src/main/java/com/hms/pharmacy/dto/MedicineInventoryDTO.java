package com.hms.pharmacy.dto;

import com.hms.pharmacy.constants.StockStatus;
import com.hms.pharmacy.entity.Medicine;
import com.hms.pharmacy.entity.MedicineInventory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicineInventoryDTO {

    private Long id;
    private Long medicineId;
    private String batchNo;
    private Integer quantity;
    private LocalDate expireDate;
    private LocalDate addedDate;
    private Integer initialQuantity;
    private StockStatus stockStatus;

    public MedicineInventory toEntity(){
        return new MedicineInventory(
                id,new Medicine(medicineId),
                batchNo,
                quantity,
                expireDate,
                addedDate,
                initialQuantity,stockStatus);
    }
}
