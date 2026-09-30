package com.example.profile.service;

import com.example.profile.entity.Hospital;

import java.util.List;

public interface HospitalService {

    List<Hospital> getAllHospitals();
    List<Hospital> getHospitalsByCity(String city);
    List<String> getAllCities();
    Hospital getHospitalById(Long id);
    Hospital addHospital(Hospital hospital);
    Hospital updateHospital(Long id, Hospital updated);
    void deleteHospital(Long id);
}
