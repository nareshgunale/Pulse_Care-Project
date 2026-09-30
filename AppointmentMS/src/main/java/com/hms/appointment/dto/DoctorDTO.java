package com.hms.appointment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDTO {

    private Long id;
    private String name;
    private String email;
    private LocalDate dob;
    private String phoneNo;
    private String address;
    private String licenseNumber;
    private String specialization;
    private String department;
    private Integer totalExperience;
    private Long hospitalId;
    private String hospitalName;
    private String city;
}