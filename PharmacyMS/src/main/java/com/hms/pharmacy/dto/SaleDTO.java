package com.hms.pharmacy.dto;

import com.hms.pharmacy.entity.Sale;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleDTO {
    private Long id;
    private Long prescriptionId;
    private LocalDateTime saleDate;
    private Double totalAmount;

    public Sale toEntity()
    {
        return new Sale(id, prescriptionId, saleDate, totalAmount);
    }
}
