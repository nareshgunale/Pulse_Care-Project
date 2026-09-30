package com.example.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// ProfileMS - com.example.profile.dto.CreateDoctorDTO.java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateDoctorDTO {
    private Long userId;
    private String name;
    private String email;
}