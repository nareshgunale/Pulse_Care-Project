package com.hms.pharmacy.service.serviceImp;

import com.hms.pharmacy.constants.StockStatus;
import com.hms.pharmacy.dto.MedicineInventoryDTO;
import com.hms.pharmacy.entity.MedicineInventory;
import com.hms.pharmacy.exception.HMSException;
import com.hms.pharmacy.repository.MedicineInventoryRepository;
import com.hms.pharmacy.service.MedicineInventoryService;
import com.hms.pharmacy.service.MedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MedicineInventoryServiceImp implements MedicineInventoryService {

    private final MedicineInventoryRepository medicineInventoryRepository;

    private final MedicineService medicineService;


    @Override
    public List<MedicineInventoryDTO> getAllMedicines() {
        return medicineInventoryRepository.findAll().stream()
                .map(inv -> {
                    if (inv.getExpireDate() != null &&
                            inv.getExpireDate().isBefore(LocalDate.now())) {
                        inv.setStockStatus(StockStatus.EXPIRED);
                    }
                    return inv.toDto();
                })
                .toList();
    }

    @Override
    public MedicineInventoryDTO getMedicineById(Long id) {
        return medicineInventoryRepository.findById(id)
                .orElseThrow(()->new HMSException("INVENTORY_NOT_FOUND")).toDto();
    }

    @Override
    public MedicineInventoryDTO addMedicine(MedicineInventoryDTO medicine) {
        medicine.setAddedDate(LocalDate.now());
        medicineService.addStock(medicine.getMedicineId(), medicine.getQuantity());
        medicine.setInitialQuantity(medicine.getQuantity());
        medicine.setStockStatus(StockStatus.ACTIVE);
        return medicineInventoryRepository.save(medicine.toEntity()).toDto();
    }

    @Override
    public MedicineInventoryDTO updateMedicine(MedicineInventoryDTO medicine) {
        MedicineInventory exitingInventory = medicineInventoryRepository.findById(medicine.getId())
                .orElseThrow(() -> new HMSException("INVENTORY_NOT_FOUND"));

        exitingInventory.setBatchNo(medicine.getBatchNo());
        exitingInventory.setQuantity(medicine.getQuantity());
        exitingInventory.setInitialQuantity(medicine.getQuantity());
        exitingInventory.setExpireDate(medicine.getExpireDate());

        //expiry check on update
        if (medicine.getExpireDate() != null &&
                medicine.getExpireDate().isBefore(LocalDate.now())) {
            exitingInventory.setStockStatus(StockStatus.EXPIRED);
        } else {
            exitingInventory.setStockStatus(StockStatus.ACTIVE);
        }

        // quantity update
        if (exitingInventory.getQuantity() < medicine.getQuantity()) {
            medicineService.addStock(medicine.getMedicineId(),
                    medicine.getQuantity() - exitingInventory.getQuantity());
        } else if (exitingInventory.getQuantity() > medicine.getQuantity()) {
            medicineService.removeStock(medicine.getMedicineId(),
                    exitingInventory.getQuantity() - medicine.getQuantity());
        }
        return medicineInventoryRepository.save(exitingInventory).toDto();
    }

    @Override
    public void deleteMedicine(Long id) {
        medicineInventoryRepository.findById(id)
                .orElseThrow(() -> new HMSException("INVENTORY_NOT_FOUND"));
        medicineInventoryRepository.deleteById(id);
    }

    private void markExpired(List<MedicineInventory> inventories){
        for (MedicineInventory inventory : inventories) {
            inventory.setStockStatus(StockStatus.EXPIRED);
        }
        medicineInventoryRepository.saveAll(inventories);
    }

    @Override
    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteExpiredMedicines(){
        List<MedicineInventory> expiredMedicines =
                medicineInventoryRepository.findByExpireDateBefore(LocalDate.now());
        for (MedicineInventory medicine : expiredMedicines) {
            medicineService.removeStock(medicine.getMedicine().getId(),medicine.getQuantity());
        }
        this.markExpired(expiredMedicines);
    }

    // "0 30 14 * * ?"
    // seconds minutes hours dayOfMonth month dayOfWeek
}
//problem - deletion of expired medicines
//solutions - secluding