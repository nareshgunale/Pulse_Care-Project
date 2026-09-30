package com.hospital.healthpackage.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthPackageResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private String idealFor;
    private String ageGroup;
    private Integer parameters;
    private List<String> tests;
    private String imageUrl;
    private Boolean isActive;
    private Boolean isPopular;
    private LocalDateTime createdAt;
}
