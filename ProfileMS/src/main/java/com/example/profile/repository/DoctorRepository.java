package com.example.profile.repository;

import com.example.profile.dto.DoctorDropDown;
import com.example.profile.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Optional<Doctor> findByEmail(String email);

    Optional<Doctor> findByLicenseNumber(String licenseNumber);

    @Query("SELECT d.id AS id, d.name AS name FROM doctor_details d")
    List<DoctorDropDown> findAllDoctorDropDown();

    @Query("SELECT d.id AS id, d.name AS name FROM doctor_details d WHERE d.id in ?1")
    List<DoctorDropDown> findAllDoctorDropdownsByIds(List<Long> ids);

    List<Doctor> findByActiveTrue();

    List<Doctor> findByHospital_IdAndActiveTrue(Long hospitalId);

    List<Doctor> findByHospital_CityAndActiveTrue(String city);
}
