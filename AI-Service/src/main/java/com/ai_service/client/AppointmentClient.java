package com.ai_service.client;


import com.ai_service.dto.BookAppointmentRequest;
import com.ai_service.dto.PrescriptionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "APPOINTMENT")
public interface AppointmentClient {

    // Get all appointments of patient
    @GetMapping("/appointment/getAllByPatient/{patientId}")
    String getAllAppointmentsByPatient(@PathVariable Long patientId);

    // Get single appointment
    @GetMapping("/appointment/get/details/{appointmentId}")
    String getAppointmentDetails(@PathVariable Long appointmentId);

    // Get available slots
    @GetMapping("/appointment/slots/available")
    String getAvailableSlots(@RequestParam Long doctorId, @RequestParam String date);

    // Cancel appointment
    @PutMapping("/appointment/cancel/{appointmentId}")
    String cancelAppointment(@PathVariable Long appointmentId);

    //book appointment
    @PostMapping("/appointment/schedule")
    String bookAppointment(@RequestBody BookAppointmentRequest request);

    ///  this is used for RAG
    @GetMapping("/api/prescriptions/patient/{patientId}")
    List<PrescriptionDTO> getPrescriptionsByPatientId(@PathVariable Long patientId);
}
