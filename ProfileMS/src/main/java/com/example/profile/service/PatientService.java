package com.example.profile.service;

import com.example.profile.dto.PatientDTO;

import java.util.List;

public interface PatientService {
    Long addPatient(PatientDTO patientDTO);

    PatientDTO getPatientById(Long id);

    PatientDTO updatePatient(PatientDTO patientDTO);

    boolean patientExists(Long id);

    List<PatientDTO> getAllPatient();

    void deletePatientById(Long patientId);

    Long getPatientIdByUserId(Long userId);
}
