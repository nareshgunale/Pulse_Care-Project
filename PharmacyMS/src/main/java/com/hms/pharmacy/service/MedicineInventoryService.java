package com.hms.pharmacy.service;

import com.hms.pharmacy.dto.MedicineInventoryDTO;
import com.hms.pharmacy.entity.MedicineInventory;

import java.util.List;

public interface MedicineInventoryService {

    List<MedicineInventoryDTO> getAllMedicines();

    MedicineInventoryDTO getMedicineById(Long id);

    MedicineInventoryDTO addMedicine(MedicineInventoryDTO medicine);

    MedicineInventoryDTO updateMedicine(MedicineInventoryDTO medicine);

    void deleteMedicine(Long id);

    void deleteExpiredMedicines();
}
