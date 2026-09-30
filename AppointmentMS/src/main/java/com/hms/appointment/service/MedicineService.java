package com.hms.appointment.service;

import com.hms.appointment.dto.MedicineDTO;

import java.util.List;

public interface MedicineService {

    Long saveMedicine(MedicineDTO request);
    List<MedicineDTO>saveAllMedicine(List<MedicineDTO> requestList);
    List<MedicineDTO>getAllMedicinesByPrescriptionId(Long prescriptionId);
    void deleteByPrescriptionId(Long prescriptionId);
}
