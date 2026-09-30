package com.hms.appointment.controller;

import com.hms.appointment.dto.PrescriptionDTO;
import com.hms.appointment.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;


    @PostMapping
    public Long savePrescription(@RequestBody PrescriptionDTO request) {
        return prescriptionService.savePrescription(request);
    }

    @GetMapping("/{id}")
    public PrescriptionDTO getById(@PathVariable Long id) {
        return prescriptionService.getPrescriptionById(id);
    }

    @GetMapping("/appointment/{appointmentId}")
    public PrescriptionDTO getByAppointment(@PathVariable Long appointmentId) {
        return prescriptionService.getPrescriptionByAppointmentId(appointmentId);
    }

    @GetMapping("/doctor/{doctorId}")
    public List<PrescriptionDTO> getByDoctor(@PathVariable Long doctorId) {
        return prescriptionService.getAllByDoctorId(doctorId);
    }

    @GetMapping("/patient/{patientId}")
    public List<PrescriptionDTO> getByPatient(@PathVariable Long patientId) {
        return prescriptionService.getAllByPatientId(patientId);
    }

    @DeleteMapping("/appointment/{appointmentId}")
    public void deleteByAppointment(@PathVariable Long appointmentId) {
        prescriptionService.deleteByAppointmentId(appointmentId);
    }
}