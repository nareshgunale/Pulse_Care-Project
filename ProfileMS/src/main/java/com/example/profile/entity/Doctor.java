package com.example.profile.entity;


import com.example.profile.dto.DoctorDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "doctor_details")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private LocalDate dob;
    private Long profilePictureId;
    private String phoneNo;
    private String address;

    @Column(unique = true)
    private String licenseNumber;

    private String specialization;
    private String department;
    private Integer totalExperience;
    private Boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    public DoctorDTO toDTO() {
        return new DoctorDTO(
                this.id,
                this.name,
                this.email,
                this.dob,
                this.profilePictureId,
                this.phoneNo,
                this.address,
                this.licenseNumber,
                this.specialization,
                this.department,
                this.totalExperience,
                true,
                this.hospital != null ? this.hospital.getId() : null,
                this.hospital != null ? this.hospital.getName() : null,
                this.hospital != null ? this.hospital.getCity() : null
        );
    }
    //Converts a DTO (DoctorDTO) into a Database Entity (Doctor)
    //API request comes from frontend (JSON → DTO)
    //Before saving → convert DTO to Entity
}
