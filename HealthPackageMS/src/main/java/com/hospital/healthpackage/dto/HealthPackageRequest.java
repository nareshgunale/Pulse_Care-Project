package com.hospital.healthpackage.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthPackageRequest {

    @NotBlank(message = "Package name is required")
    @Size(min = 3, max = 150, message = "Name must be 3–150 characters")
    private String name;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 1000, message = "Description must be 10–1000 characters")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Invalid price format")
    private BigDecimal price;

    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "^(Male|Female|Both)$", message = "Must be Male, Female, or Both")
    private String idealFor;

    @NotBlank(message = "Age group is required")
    private String ageGroup;

    @Min(value = 1, message = "At least 1 parameter required")
    private Integer parameters;

    @NotEmpty(message = "Tests list cannot be empty")
    private List<@NotBlank(message = "Test name cannot be blank") String> tests;

    private String imageUrl;

    @Builder.Default
    private Boolean isPopular = false;
}
