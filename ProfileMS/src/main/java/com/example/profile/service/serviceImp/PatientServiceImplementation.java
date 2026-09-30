package com.example.profile.service.serviceImp;

import com.example.profile.dto.PatientDTO;
import com.example.profile.entity.Patient;
import com.example.profile.exception.HMSException;
import com.example.profile.repository.PatientRepository;
import com.example.profile.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientServiceImplementation implements PatientService {

    @Autowired
    PatientRepository patientRepository;

    @Override
    public Long addPatient(PatientDTO patientDTO) {
        if (patientDTO.getEmail() != null &&
                patientRepository.findByEmail(patientDTO.getEmail()).isPresent()) {
            throw new HMSException("PATIENT_ALREADY-EXITS");
        }

        if (patientDTO.getAadharId() != null &&
                patientRepository.findByAadharId(patientDTO.getAadharId()).isPresent()) {
            throw new HMSException("PATIENT_ALREADY-EXITS");
        }

        return patientRepository.save(patientDTO.toNewEntity()).getId();
    }

    @Override
    public PatientDTO getPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new HMSException("PATIENT_NOT_FOUND")).toDTO();
    }

    @Override
    public PatientDTO updatePatient(PatientDTO patientDTO) {
        patientRepository.findById(patientDTO.getId()).orElseThrow(() -> new HMSException("PATIENT_NOT_FOUND"));
        return patientRepository.save(patientDTO.toEntity()).toDTO();
    }

    @Override
    public boolean patientExists(Long id) {
        return patientRepository.existsById(id);
    }

    @Override
    public List<PatientDTO> getAllPatient() {
        return patientRepository.findByActiveTrue()
                .stream()
                .map(p -> new PatientDTO(
                        p.getId(),
                        p.getUserId(),
                        p.getName(),
                        p.getEmail(),
                        p.getDob(),
                        p.getProfilePictureId(),
                        p.getPhoneNo(),
                        p.getAddress(),
                        p.getAadharId(),
                        p.getBloodGroup(),
                        p.getAllergies(),
                        p.getChronicDisease(),
                        true
                )).toList();
    }

    @Override
    public void deletePatientById(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new HMSException("PATIENT_NOT_FOUND"));
        patient.setActive(false);
        patientRepository.save(patient);
    }

    @Override
    public Long getPatientIdByUserId(Long userId) {

        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        return patient.getId();
    }
}
