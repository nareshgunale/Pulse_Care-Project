package com.example.profile.repository;

import com.example.profile.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {

    List<Hospital> findByActiveTrue();

    List<Hospital> findByCityIgnoreCaseAndActiveTrue(String city);

    @Query("SELECT DISTINCT h.city FROM Hospital h WHERE h.active = true ORDER BY h.city")
    List<String> findAllActiveCities();
}