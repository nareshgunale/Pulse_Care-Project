package com.hms.pharmacy.service;

import com.hms.pharmacy.dto.SaleDTO;

public interface SaleService {
    Long createSale(SaleDTO dto);

    void updateSale(SaleDTO dto);

    SaleDTO getSale(Long id);

    SaleDTO getSaleByPrescriptionId(Long prescriptionId);
}
