package com.example.profile.controller;


import com.example.profile.entity.Hospital;
import com.example.profile.service.HospitalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hospitals")
@RequiredArgsConstructor
//@CrossOrigin(origins = "*")
public class HospitalController {

    private final HospitalService hospitalService;


    @GetMapping
    public ResponseEntity<?> getAllHospitals() {
        List<Hospital> hospitals = hospitalService.getAllHospitals();
        return ResponseEntity.ok(Map.of("success", true, "message", "Hospitals fetched",
                "data", hospitals));
    }


    @GetMapping("/city/{city}")
    public ResponseEntity<?> getByCity(@PathVariable String city) {
        List<Hospital> hospitals = hospitalService.getHospitalsByCity(city);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Hospitals in " + city,
                "data", hospitals
        ));
    }


    @GetMapping("/cities")
    public ResponseEntity<?> getAllCities() {
        List<String> cities = hospitalService.getAllCities();
        return ResponseEntity.ok(Map.of("success", true,
                "message", "Cities fetched", "data", cities));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Hospital hospital = hospitalService.getHospitalById(id);
        return ResponseEntity.ok(Map.of("success", true, "data", hospital));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addHospital(@Valid @RequestBody Hospital hospital) {
        Hospital saved = hospitalService.addHospital(hospital);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("success", true, "message", "Hospital added successfully",
                "data", saved));
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateHospital(@PathVariable Long id, @Valid @RequestBody Hospital hospital) {
        Hospital updated = hospitalService.updateHospital(id, hospital);
        return ResponseEntity.ok(Map.of("success", true, "message", "Hospital updated",
                "data", updated));
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteHospital(@PathVariable Long id) {
        hospitalService.deleteHospital(id);
        return ResponseEntity.ok(Map.of("success", true, "message", "Hospital deleted"));
    }
}