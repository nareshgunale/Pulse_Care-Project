package com.example.profile.dto;

import com.example.profile.entity.Doctor;
import com.example.profile.entity.Hospital;
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
    private Long profilePictureId;
    private String phoneNo;
    private String address;
    private String licenseNumber;
    private String specialization;
    private String department;
    private Integer totalExperience;
    private Boolean active;

    private Long hospitalId;
    private String hospitalName;
    private String city;

    public Doctor toEntity() {
        Doctor doctor = new Doctor();
        doctor.setId(this.id);
        doctor.setName(this.name);
        doctor.setEmail(this.email);
        doctor.setDob(this.dob);
        doctor.setProfilePictureId(this.profilePictureId);
        doctor.setPhoneNo(this.phoneNo);
        doctor.setAddress(this.address);
        doctor.setLicenseNumber(this.licenseNumber);
        doctor.setSpecialization(this.specialization);
        doctor.setDepartment(this.department);
        doctor.setTotalExperience(this.totalExperience);
        doctor.setActive(true);
        return doctor;
    }
    //Converts Entity (Doctor) into DTO (DoctorDTO)
    //Fetch data from DB (Entity)
    //Convert → DTO → send to frontend


}
