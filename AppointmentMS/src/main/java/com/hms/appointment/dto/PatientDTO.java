package com.hms.appointment.dto;

import com.hms.appointment.constant.BloodGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientDTO {
    private Long id;
    private String name;
    private String email;
    private LocalDate dob;
    private String phoneNo;
    private String address;
    private String aadharId;
    private BloodGroup bloodGroup;
    private String allergies;
    private String chronicDisease;
}
