package com.hms.appointment.controller;

import com.hms.appointment.entity.DoctorSchedule;
import com.hms.appointment.repository.DoctorScheduleRepository;
import com.hms.appointment.service.DoctorScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/appointment/schedule")
@RequiredArgsConstructor
public class DoctorScheduleController {

    private final DoctorScheduleService doctorScheduleService;

    // Admin set doctor schedule
    @PostMapping("/set")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DoctorSchedule> setSchedule(@RequestBody DoctorSchedule schedule) {
        return new ResponseEntity<>(doctorScheduleService.saveOrUpdate(schedule), HttpStatus.CREATED);
    }

    //check doctor schedule
    @GetMapping
    public ResponseEntity<List<DoctorSchedule>> getAllSchedules() {
        return ResponseEntity.ok(doctorScheduleService.findAll());
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<DoctorSchedule>> getSchedule(@PathVariable Long doctorId) {
        return ResponseEntity.ok(doctorScheduleService.findByDoctorId(doctorId));
    }
}