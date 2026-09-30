package com.hms.pharmacy.service;

import com.hms.pharmacy.dto.SaleItemDTO;

import java.util.List;

public interface SaleItemService {

    Long createSaleItem(SaleItemDTO saleItemDTO);

    void createMultipleSaleItem(Long saleId, Long medicineId, List<SaleItemDTO> saleItemDTOs);

    void updateSaleItem(SaleItemDTO saleItemDTO);

    List<SaleItemDTO> getSaleItemsBySaleId(Long saleId);

    SaleItemDTO getSaleItem(Long id);
}
