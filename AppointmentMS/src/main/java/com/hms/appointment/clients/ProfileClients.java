package com.hms.appointment.clients;

import com.hms.appointment.dto.DoctorDTO;
import com.hms.appointment.dto.DoctorName;
import com.hms.appointment.dto.PatientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "ProfileMS")
public interface ProfileClients {

    @GetMapping("/profile/doctor/exists/{id}")
    Boolean doctorExists(@PathVariable("id") Long id);

    @GetMapping("/profile/patient/exists/{id}")
    Boolean patientExists(@PathVariable("id") Long id);

    @GetMapping("/profile/patient/get/{id}")
    PatientDTO getPatientById(@PathVariable("id") Long id);

    @GetMapping("/profile/doctor/get/{id}")
    DoctorDTO getDoctorById(@PathVariable("id") Long id);

        @GetMapping("/profile/doctor/getDoctorsById")
    List<DoctorName> getDoctorsById(@RequestParam List<Long> ids);

// Missing: add this for getAllAppointmentByDoctorId batch fix
    @GetMapping("/profile/patient/getPatientsByIds")
    List<PatientDTO> getPatientsByIds(@RequestParam List<Long> ids);
}
