package com.hms.pharmacy.service;

import com.hms.pharmacy.dto.MedicineDTO;

import java.util.List;

public interface MedicineService {

    Long addMedicine(MedicineDTO medicineDTO);

    MedicineDTO getMedicineById(Long id);

    void updateMedicine(MedicineDTO medicineDTO);

    List<MedicineDTO>getAllMedicines();

    Integer getStockById(Long id);

    Integer addStock(Long id, Integer quantity);

    Integer removeStock(Long id, Integer quantity);

    void deleteMedicine(Long id);
}
