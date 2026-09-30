package com.hms.appointment.controller;

import com.hms.appointment.dto.AppointmentDTO;
import com.hms.appointment.dto.AppointmentDetailsDTO;
import com.hms.appointment.entity.DoctorSchedule;
import com.hms.appointment.exception.HMSException;
import com.hms.appointment.repository.AppointmentRepository;
import com.hms.appointment.repository.DoctorScheduleRepository;
import com.hms.appointment.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/appointment")
@Validated
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    private final DoctorScheduleRepository doctorScheduleRepository;
    private final AppointmentRepository appointmentRepository;

    @PostMapping("/schedule")
    public ResponseEntity<Long> scheduleAppointment(@RequestBody AppointmentDTO dto) {
        return new ResponseEntity<>(appointmentService.scheduleAppointment(dto), HttpStatus.CREATED);
    }

    @PutMapping("/cancel/{appointmentId}")
    public ResponseEntity<String> cancelAppoint(@PathVariable("appointmentId") Long id) {
        appointmentService.cancelAppointment(id);
        return new ResponseEntity<>("Appointment Cancel", HttpStatus.OK);
    }

    @GetMapping("/get/{appointmentId}")
    public ResponseEntity<AppointmentDTO> getAppointment(@PathVariable("appointmentId") Long id) {
        return new ResponseEntity<>(appointmentService.getAppointmentDetails(id), HttpStatus.OK);
    }

    @GetMapping("/get/details/{appointmentId}")
    public ResponseEntity<AppointmentDetailsDTO> getAppointmentDetailsWithName(@PathVariable("appointmentId") Long appointmentId) {
        return new ResponseEntity<>(
                appointmentService.getAppointmentDetailWithName(appointmentId), HttpStatus.OK);
    }

    @GetMapping("/getAllByPatient/{patientId}")
    public ResponseEntity<List<AppointmentDetailsDTO>> getAllAppointmentByPatientId(@PathVariable("patientId") Long patientId) {
        return new ResponseEntity<>(appointmentService.getAllAppointmentByPatientId(patientId), HttpStatus.OK);
    }

    @GetMapping("/getAllByDoctor/{doctorId}")
    public ResponseEntity<List<AppointmentDetailsDTO>> getAllAppointmentByDoctorId(@PathVariable("doctorId") Long doctorId) {
        return new ResponseEntity<>(appointmentService.getAllAppointmentByDoctorId(doctorId), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<AppointmentDTO>> getAllAppointments() {
        return new ResponseEntity<>(appointmentService.getAllAppointments(), HttpStatus.OK);
    }

    @GetMapping("/all/details")
    public ResponseEntity<List<AppointmentDTO>> getAllAppointmentDetails() {
        return new ResponseEntity<>(appointmentService.getAllAppointmentDetails(), HttpStatus.OK);
    }

    @GetMapping("/slots/available")
    public ResponseEntity<List<String>> getAvailableSlots(@RequestParam Long doctorId,
                                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                          LocalDate date) {
        return ResponseEntity.ok(appointmentService.getAvailableSlots(doctorId, date));
    }

    @GetMapping("/slots/all")
    public ResponseEntity<Map<String, Object>> getAllSlots(@RequestParam Long doctorId,
                                                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                           LocalDate date) {
        return ResponseEntity.ok(appointmentService.getAllSlots(doctorId, date));
    }
}
