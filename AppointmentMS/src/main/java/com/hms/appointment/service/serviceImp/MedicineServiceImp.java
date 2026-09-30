package com.hms.appointment.service.serviceImp;

import com.hms.appointment.dto.MedicineDTO;
import com.hms.appointment.entity.Medicine;
import com.hms.appointment.repository.MedicineRepository;
import com.hms.appointment.service.MedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MedicineServiceImp implements MedicineService {

    private final MedicineRepository medicineRepository;

    @Override
    public Long saveMedicine(MedicineDTO request) {
        return medicineRepository.save(request.toEntity()).getId();
    }

    @Override
    public List<MedicineDTO> saveAllMedicine(List<MedicineDTO> requestList) {
        return medicineRepository.saveAll(
                requestList.stream().map(MedicineDTO::toEntity).toList()
        ).stream().map(Medicine::toDTO).toList();
    }

    @Override
    public List<MedicineDTO> getAllMedicinesByPrescriptionId(Long prescriptionId) {
        return medicineRepository.findAllByPrescription_Id(prescriptionId)
                .stream().map(Medicine::toDTO).toList();
    }

    @Override
    public void deleteByPrescriptionId(Long prescriptionId) {
        List<Medicine> medicines = medicineRepository.findAllByPrescription_Id(prescriptionId);
        medicineRepository.deleteAll(medicines);
    }
}
