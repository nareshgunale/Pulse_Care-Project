package com.hospital.healthpackage.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "health_packages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Package name is required")
    @Size(min = 3, max = 150, message = "Package name must be between 3 and 150 characters")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Invalid price format")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @NotBlank(message = "Category is required")
    @Column(nullable = false)
    private String category;

    @NotBlank(message = "Gender suitability is required")
    @Pattern(regexp = "^(Male|Female|Both)$", message = "Gender must be Male, Female, or Both")
    @Column(nullable = false)
    private String idealFor;

    @NotBlank(message = "Age group is required")
    @Column(nullable = false)
    private String ageGroup;

    @Min(value = 1, message = "At least 1 test parameter is required")
    @Column(nullable = false)
    private Integer parameters;

    @ElementCollection
    @CollectionTable(name = "package_tests", joinColumns = @JoinColumn(name = "package_id"))
    @Column(name = "test_name")
    @NotEmpty(message = "At least one test must be included")
    private List<@NotBlank(message = "Test name cannot be blank") String> tests;

    @Column
    private String imageUrl;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isPopular = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
