package com.example.profile.service;

import com.example.profile.dto.DoctorDTO;
import com.example.profile.dto.DoctorDropDown;

import java.util.List;


public interface DoctorService {
    Long addDoctor(DoctorDTO doctorDTO);

    DoctorDTO getDoctorById(Long id);

    DoctorDTO updateDoctor(DoctorDTO doctorDTO);

    boolean doctorExists(Long id);

    List<DoctorDTO> getAllDoctors();

    List<DoctorDropDown> getDropDown();

    void deleteDoctorById(Long doctorId);

    List<DoctorDropDown> getDoctorById(List<Long> ids);

    List<DoctorDTO> getDoctorsByHospital(Long hospitalId);

    List<DoctorDTO> getDoctorsByCity(String city);
}
