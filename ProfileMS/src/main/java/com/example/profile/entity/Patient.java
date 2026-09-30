package com.example.profile.entity;

import com.example.profile.constant.BloodGroup;
import com.example.profile.dto.PatientDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "patient_detail")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String name;

    @Column(unique = true)
    private String email;

    private LocalDate dob;
    private Long profilePictureId;
    private String phoneNo;
    private String address;

    @Column(unique = true)
    private String aadharId;
    private BloodGroup bloodGroup;
    private String allergies;
    private String chronicDisease;
    private Boolean active = true;

    public PatientDTO toDTO(){
        return new PatientDTO(this.id,this.userId, this.name, this.email,this.dob,this.profilePictureId
                ,this.phoneNo,this.address,this.aadharId,this.bloodGroup,this.allergies,this.chronicDisease,true);
    }
}
