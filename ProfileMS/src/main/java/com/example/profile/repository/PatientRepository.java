package com.example.profile.repository;

import com.example.profile.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByEmail(String email);

    Optional<Patient> findByAadharId(String aadharId);

    List<Patient> findByActiveTrue();

    Optional<Patient> findByUserId(Long userId);
}
