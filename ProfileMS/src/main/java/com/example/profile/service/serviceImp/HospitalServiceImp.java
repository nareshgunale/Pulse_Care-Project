package com.example.profile.service.serviceImp;

import com.example.profile.entity.Hospital;
import com.example.profile.exception.HMSException;
import com.example.profile.repository.HospitalRepository;
import com.example.profile.service.HospitalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HospitalServiceImp implements HospitalService {

    private final HospitalRepository hospitalRepository;

    @Override
    public List<Hospital> getAllHospitals() {
        List<Hospital> hospitals = hospitalRepository.findByActiveTrue();
        return hospitals != null ? hospitals : Collections.emptyList();
    }

    @Override
    public List<Hospital> getHospitalsByCity(String city) {
        if (city == null || city.trim().isEmpty()) {
            throw new HMSException("CITY_REQUIRED");
        }
        List<Hospital> hospitals = hospitalRepository.findByCityIgnoreCaseAndActiveTrue(city.trim());
        return hospitals != null ? hospitals : Collections.emptyList();
    }

    @Override
    public List<String> getAllCities() {
        List<String> cities = hospitalRepository.findAllActiveCities();
        return cities != null ? cities : Collections.emptyList();
    }

    @Override
    public Hospital getHospitalById(Long id) {
        if (id == null) {
            throw new HMSException("HOSPITAL_ID_REQUIRED");
        }
        return hospitalRepository.findById(id)
                .orElseThrow(() -> new HMSException("HOSPITAL_NOT_FOUND"));
    }

    @Override
    public Hospital addHospital(Hospital hospital) {
        if (hospital == null) {
            throw new HMSException("HOSPITAL_DATA_REQUIRED");
        }

        boolean exists = hospitalRepository.findByActiveTrue().stream()
                .anyMatch(h -> h.getName().equalsIgnoreCase(hospital.getName())
                        && h.getCity().equalsIgnoreCase(hospital.getCity()));

        if (exists) {
            throw new HMSException("HOSPITAL_ALREADY_EXISTS");
        }
        hospital.setId(null);
        hospital.setActive(true);
        return hospitalRepository.save(hospital);
    }

    @Override
    public Hospital updateHospital(Long id, Hospital updated) {
        if (updated == null) {
            throw new HMSException("HOSPITAL_DATA_REQUIRED");
        }

        Hospital existing = getHospitalById(id);

        if (updated.getName() != null && !updated.getName().trim().isEmpty()) {
            existing.setName(updated.getName());
        }
        if (updated.getCity() != null && !updated.getCity().trim().isEmpty()) {
            existing.setCity(updated.getCity());
        }
        if (updated.getAddress() != null && !updated.getAddress().trim().isEmpty()) {
            existing.setAddress(updated.getAddress());
        }
        if (updated.getPhone() != null && !updated.getPhone().trim().isEmpty()) {
            existing.setPhone(updated.getPhone());
        }
        if (updated.getEmail() != null && !updated.getEmail().trim().isEmpty()) {
            existing.setEmail(updated.getEmail());
        }
        if (updated.getImageUrl() != null) {
            existing.setImageUrl(updated.getImageUrl());
        }
        if (updated.getFacilities() != null) {
            existing.setFacilities(updated.getFacilities());
        }

        return hospitalRepository.save(existing);
    }

    @Override
    public void deleteHospital(Long id) {
        Hospital existing = getHospitalById(id);
        existing.setActive(false);
        hospitalRepository.save(existing);
    }
}