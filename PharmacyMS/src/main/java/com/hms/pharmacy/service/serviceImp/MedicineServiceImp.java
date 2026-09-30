package com.hms.pharmacy.service.serviceImp;

import com.hms.pharmacy.dto.MedicineDTO;
import com.hms.pharmacy.entity.Medicine;
import com.hms.pharmacy.exception.HMSException;
import com.hms.pharmacy.repository.MedicineRepository;
import com.hms.pharmacy.service.MedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MedicineServiceImp implements MedicineService {

    private final MedicineRepository medicineRepository;

    @Override
    public Long addMedicine(MedicineDTO medicineDTO) {
        Optional<Medicine> medicine = medicineRepository.findByNameIgnoreCaseAndDosageIgnoreCase(medicineDTO.getName(),
                medicineDTO.getDosage());
        if (medicine.isPresent()){
            throw new HMSException("MEDICINE_ALREADY_EXISTS");
        }
//        medicineDTO.setStock(0);
        medicineDTO.setCreateAt(LocalDateTime.now());
        return medicineRepository.save(medicineDTO.toEntity()).getId();
    }

    @Override
    public MedicineDTO getMedicineById(Long id) {
        return medicineRepository.findById(id)
                .orElseThrow(()->new HMSException("MEDICINE_NOT_FOUND")).toDto();
    }

    @Override
    public void updateMedicine(MedicineDTO medicineDTO) {
        Medicine existingMedicine = medicineRepository.findById(medicineDTO.getId())
                .orElseThrow(() -> new HMSException("MEDICINE_NOT_FOUND"));

        if (!(medicineDTO.getName().equalsIgnoreCase(existingMedicine.getName())
                && medicineDTO.getDosage().equalsIgnoreCase(existingMedicine.getDosage()))) {
            Optional<Medicine> optional =
                    medicineRepository.findByNameIgnoreCaseAndDosageIgnoreCase(
                            medicineDTO.getName(), medicineDTO.getDosage());
            if (optional.isPresent()) {
                throw new HMSException("MEDICINE_ALREADY_EXISTS");
            }
        }
        existingMedicine.setName(medicineDTO.getName());
        existingMedicine.setDosage(medicineDTO.getDosage());
        existingMedicine.setCategory(medicineDTO.getCategory());
        existingMedicine.setType(medicineDTO.getType());
        existingMedicine.setManufacturer(medicineDTO.getManufacturer());
        existingMedicine.setUnitPrice(medicineDTO.getUnitPrice());
        existingMedicine.setCreateAt(medicineDTO.getCreateAt());
        medicineRepository.save(existingMedicine);
    }

    @Override
    public List<MedicineDTO> getAllMedicines() {
        return medicineRepository.findAll().stream()
                .map(Medicine::toDto).toList();
    }

    @Override
    public Integer getStockById(Long id) {
        return medicineRepository.findStockById(id)
                .orElseThrow(()->new HMSException("MEDICINE_NOT_FOUND"));
    }

    @Override
    public Integer addStock(Long id, Integer quantity) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new HMSException("MEDICINE_NOT_FOUND"));
        medicine.setStock(medicine.getStock() != null ? medicine.getStock() + quantity : quantity);
        medicineRepository.save(medicine);
        return medicine.getStock();
    }

    @Override
    public Integer removeStock(Long id, Integer quantity) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new HMSException("MEDICINE_NOT_FOUND"));
        medicine.setStock(medicine.getStock() != null ? medicine.getStock() - quantity : quantity);
        medicineRepository.save(medicine);
        return medicine.getStock();
    }

    @Override
    public void deleteMedicine(Long id) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new HMSException("MEDICINE_NOT_FOUND"));
        medicineRepository.delete(medicine);
    }
}
