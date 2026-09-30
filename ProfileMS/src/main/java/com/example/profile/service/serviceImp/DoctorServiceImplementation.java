package com.example.profile.service.serviceImp;

import com.example.profile.dto.DoctorDTO;
import com.example.profile.dto.DoctorDropDown;
import com.example.profile.entity.Doctor;
import com.example.profile.entity.Hospital;
import com.example.profile.exception.HMSException;
import com.example.profile.repository.DoctorRepository;
import com.example.profile.repository.HospitalRepository;
import com.example.profile.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class DoctorServiceImplementation implements DoctorService {

    @Autowired
    DoctorRepository doctorRepository;

    @Autowired
    HospitalRepository hospitalRepository;

    @Override
    public Long addDoctor(DoctorDTO doctorDTO) {
        if (doctorDTO.getEmail() != null && doctorRepository.findByEmail(doctorDTO.getEmail()).isPresent())
            throw new HMSException("DOCTOR_ALREADY_EXISTS");

        Doctor doctor = doctorDTO.toEntity();
        doctor.setId(null);

        if (doctorDTO.getHospitalId() != null) {
            Hospital hospital = hospitalRepository.findById(doctorDTO.getHospitalId())
                    .orElseThrow(() -> new HMSException("HOSPITAL_NOT_FOUND"));
            doctor.setHospital(hospital);
        }

        return doctorRepository.save(doctor).getId();
    }

    @Override
    public DoctorDTO getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new HMSException("DOCTOR_NOT_FOUND")).toDTO();
    }

    @Override
    public DoctorDTO updateDoctor(DoctorDTO doctorDTO) {
        Doctor existing = doctorRepository.findById(doctorDTO.getId())
                .orElseThrow(() -> new HMSException("DOCTOR_NOT_FOUND"));


        existing.setName(doctorDTO.getName());
        existing.setEmail(doctorDTO.getEmail());
        existing.setDob(doctorDTO.getDob());
        existing.setPhoneNo(doctorDTO.getPhoneNo());
        existing.setAddress(doctorDTO.getAddress());
        existing.setSpecialization(doctorDTO.getSpecialization());
        existing.setDepartment(doctorDTO.getDepartment());
        existing.setTotalExperience(doctorDTO.getTotalExperience());
        existing.setActive(doctorDTO.getActive() != null ? doctorDTO.getActive() : true);

        if (doctorDTO.getHospitalId() != null) {
            Hospital hospital = hospitalRepository.findById(doctorDTO.getHospitalId())
                    .orElseThrow(() -> new HMSException("HOSPITAL_NOT_FOUND"));
            existing.setHospital(hospital);
        }

        return doctorRepository.save(existing).toDTO();
    }

    @Override
    public boolean doctorExists(Long id) {
        return doctorRepository.findById(id)
                .map(Doctor::getActive)
                .orElse(false);
    }

    @Override
    public List<DoctorDTO> getAllDoctors() {
        return doctorRepository.findByActiveTrue()
                .stream()
                .map(Doctor::toDTO)
                .toList();
    }

    @Override
    public List<DoctorDropDown> getDropDown() {
        return doctorRepository.findAllDoctorDropDown();
    }

    @Override
    public void deleteDoctorById(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new HMSException("DOCTOR_NOT_FOUND"));
        doctor.setActive(false);
        doctorRepository.save(doctor);
    }

    @Override
    public List<DoctorDropDown> getDoctorById(List<Long> ids) {
        return doctorRepository.findAllDoctorDropdownsByIds(ids);
    }

    @Override
    public List<DoctorDTO> getDoctorsByHospital(Long hospitalId) {
        if (hospitalId == null) {
            throw new HMSException("HOSPITAL_ID_REQUIRED");
        }
        List<Doctor> doctors = doctorRepository.findByHospital_IdAndActiveTrue(hospitalId);
        if (doctors == null || doctors.isEmpty()) {
            return Collections.emptyList();
        }
        return doctors.stream().map(Doctor::toDTO).toList();
    }

    @Override
    public List<DoctorDTO> getDoctorsByCity(String city) {
        if (city == null || city.trim().isEmpty()) {
            throw new HMSException("CITY_REQUIRED");
        }
        List<Doctor> doctors = doctorRepository.findByHospital_CityAndActiveTrue(city.trim());
        if (doctors == null || doctors.isEmpty()) {
            return Collections.emptyList();
        }
        return doctors.stream().map(Doctor::toDTO).toList();
    }
}
