package com.example.profile.controller;

import com.example.profile.dto.DoctorDTO;
import com.example.profile.dto.DoctorDropDown;
import com.example.profile.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/profile/doctor")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> addDoctor(@RequestBody DoctorDTO doctorDTO) {
        return new ResponseEntity<>(doctorService.addDoctor(doctorDTO), HttpStatus.CREATED);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<DoctorDTO> getDoctor(@PathVariable("id") Long id) {
        return new ResponseEntity<>(doctorService.getDoctorById(id), HttpStatus.OK);
    }

    @PutMapping("/update")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DoctorDTO> updateDoctor(@RequestBody DoctorDTO doctorDTO) {
        return new ResponseEntity<>(doctorService.updateDoctor(doctorDTO), HttpStatus.OK);
    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existsDoctor(@PathVariable("id") Long id) {
        return new ResponseEntity<>(doctorService.doctorExists(id), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<DoctorDTO>> getAllDoctor() {
        return new ResponseEntity<>(doctorService.getAllDoctors(), HttpStatus.OK);
    }

    @GetMapping("/dropdowns")
    public ResponseEntity<List<DoctorDropDown>> getDropDown() {
        return new ResponseEntity<>(doctorService.getDropDown(), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{doctorId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteDoctorById(@PathVariable("doctorId") Long doctorId) {
        doctorService.deleteDoctorById(doctorId);
        return new ResponseEntity<>("Doctor Delete Successfully", HttpStatus.OK);
    }

    @GetMapping("/getDoctorsById")
    public ResponseEntity<List<DoctorDropDown>> getDoctorsById(@RequestParam List<Long> ids) {
        return new ResponseEntity<>(doctorService.getDoctorById(ids), HttpStatus.OK);
    }

    @GetMapping("/by-hospital/{hospitalId}")
    public ResponseEntity<List<DoctorDTO>> getDoctorsByHospital(@PathVariable Long hospitalId) {
        return new ResponseEntity<>(doctorService.getDoctorsByHospital(hospitalId), HttpStatus.OK);
    }

    @GetMapping("/by-city/{city}")
    public ResponseEntity<List<DoctorDTO>> getDoctorsByCity(@PathVariable String city) {
        return new ResponseEntity<>(doctorService.getDoctorsByCity(city), HttpStatus.OK);
    }
}
