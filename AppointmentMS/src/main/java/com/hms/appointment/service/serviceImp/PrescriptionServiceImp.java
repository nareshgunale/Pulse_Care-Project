package com.hms.appointment.service.serviceImp;

import com.hms.appointment.clients.ProfileClients;
import com.hms.appointment.dto.DoctorDTO;
import com.hms.appointment.dto.MedicineDTO;
import com.hms.appointment.dto.PrescriptionDTO;
import com.hms.appointment.entity.Prescription;
import com.hms.appointment.exception.HMSException;
import com.hms.appointment.repository.MedicineRepository;
import com.hms.appointment.repository.PrescriptionRepository;
import com.hms.appointment.service.MedicineService;
import com.hms.appointment.service.PrescriptionService;
import com.hms.appointment.service.S3ArchiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PrescriptionServiceImp implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;

    private final MedicineService medicineService;

    private final ProfileClients profileClients;

    private final S3ArchiveService s3ArchiveService;


    @Override
    public Long savePrescription(PrescriptionDTO request) {
        // Set current date as prescription date
        request.setPrescriptionDate(LocalDate.now());
        if (request.getDoctorName() == null || request.getDoctorName().isBlank()) {
            DoctorDTO doctor = profileClients.getDoctorById(request.getDoctorId());
            if (doctor != null) {
                request.setDoctorName(doctor.getName());
            }
        }
        Long prescriptionId = prescriptionRepository.save(request.toEntity()).getId();
        // Set prescription id inside every medicine
        request.getMedicines().forEach(medicine -> {
            medicine.setPrescriptionId(prescriptionId);
        });
        medicineService.saveAllMedicine(request.getMedicines());
        // Set generated id back into DTO
        request.setId(prescriptionId);
        return prescriptionId;
    }

    @Override
    public PrescriptionDTO getPrescriptionByAppointmentId(Long appointmentId) {
        PrescriptionDTO prescriptionDTO = prescriptionRepository.findByAppointment_Id(appointmentId)
                .orElseThrow(() -> new HMSException("PRESCRIPTION_NOT_FOUND")).toDTO();
        prescriptionDTO.setMedicines(medicineService.getAllMedicinesByPrescriptionId(prescriptionDTO.getId()));
        return prescriptionDTO;
    }

    @Override
    public PrescriptionDTO getPrescriptionById(Long prescriptionId) {
        PrescriptionDTO prescriptionDTO = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new HMSException("PRESCRIPTION_NOT_FOUND")).toDTO();
        prescriptionDTO.setMedicines(medicineService.getAllMedicinesByPrescriptionId(prescriptionDTO.getId()));
        return prescriptionDTO;
    }

    @Override
    public void deleteByAppointmentId(Long appointmentId) {
        Prescription prescription = prescriptionRepository.findByAppointment_Id(appointmentId)
                .orElseThrow(() -> new HMSException("PRESCRIPTION_NOT_FOUND"));
        medicineService.deleteByPrescriptionId(prescription.getId());
        prescriptionRepository.delete(prescription);
    }

    @Override
    public List<PrescriptionDTO> getAllByDoctorId(Long doctorId) {
        List<Prescription> list =
                prescriptionRepository.findAllByDoctorIdOrderByPrescriptionDateDesc(doctorId);

        return list.stream().map(p -> {
            PrescriptionDTO dto;

            if (p.isArchived() && p.getS3Key() != null) {
                dto = s3ArchiveService.fetchFromS3(p.getS3Key());
                dto.setArchived(true);
                dto.setS3Key(p.getS3Key());
            } else {
                dto = p.toDTO();
                dto.setMedicines(
                        medicineService.getAllMedicinesByPrescriptionId(p.getId())
                );
            }

            return dto;
        }).toList();
    }

    @Override
    public List<PrescriptionDTO> getAllByPatientId(Long patientId) {
        return prescriptionRepository.findAllByPatientId(patientId)
                .stream()
                .map(p -> {
                    PrescriptionDTO dto;

                    if (p.isArchived() && p.getS3Key() != null) {
                        dto = s3ArchiveService.fetchFromS3(p.getS3Key());
                        dto.setArchived(true);
                        dto.setS3Key(p.getS3Key());
                    } else {
                        dto = p.toDTO();
                        dto.setMedicines(
                                medicineService.getAllMedicinesByPrescriptionId(p.getId())
                        );
                    }

                    if (dto.getDoctorName() == null || dto.getDoctorName().isBlank()) {
                        DoctorDTO doctor = profileClients.getDoctorById(dto.getDoctorId());
                        if (doctor != null) dto.setDoctorName(doctor.getName());
                    }

                    return dto;
                })
                .toList();
    }
}
