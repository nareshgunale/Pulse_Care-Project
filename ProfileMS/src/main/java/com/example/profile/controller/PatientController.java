package com.example.profile.controller;

import com.example.profile.dto.PatientDTO;
import com.example.profile.dto.PatientProfileRequest;
import com.example.profile.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequestMapping("/profile/patient")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @PostMapping("/add")
    public ResponseEntity<Long> addPatient(
            @RequestBody PatientProfileRequest request) {

        PatientDTO patientDTO = new PatientDTO();

        patientDTO.setUserId(request.getUserId());
        patientDTO.setName(request.getName());
        patientDTO.setEmail(request.getEmail());
        patientDTO.setActive(true);

        Long patientId = patientService.addPatient(patientDTO);

        return new ResponseEntity<>(patientId, HttpStatus.CREATED);
    }
    @GetMapping("/get/{id}")
    public ResponseEntity<PatientDTO> getPatientById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(patientService.getPatientById(id), HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<PatientDTO> updatePatient(@RequestBody PatientDTO patientDTO) {
        return new ResponseEntity<>(patientService.updatePatient(patientDTO), HttpStatus.OK);
    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existsPatient(@PathVariable("id") Long id) {
        return new ResponseEntity<>(patientService.patientExists(id), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<PatientDTO>> getAllPatient() {
        return new ResponseEntity<>(patientService.getAllPatient(), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{patientId}")
    public ResponseEntity<String> deletePatientById(@PathVariable("patientId") Long patientId) {
        patientService.deletePatientById(patientId);
        return new ResponseEntity<>("Patient Delete Successfully", HttpStatus.OK);
    }

    @GetMapping("/patient-id/{userId}")
    public ResponseEntity<Long> getPatientIdByUserId(@PathVariable Long userId) {

        return ResponseEntity.ok(patientService.getPatientIdByUserId(userId));

    }
}
