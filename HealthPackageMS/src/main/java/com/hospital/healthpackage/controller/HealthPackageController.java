package com.hospital.healthpackage.controller;

import com.hospital.healthpackage.dto.ApiResponse;
import com.hospital.healthpackage.dto.HealthPackageRequest;
import com.hospital.healthpackage.dto.HealthPackageResponse;
import com.hospital.healthpackage.service.HealthPackageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/packages")
@RequiredArgsConstructor
//@CrossOrigin(origins = "*")
public class HealthPackageController {

    private final HealthPackageService service;


    @GetMapping
    public ResponseEntity<ApiResponse<List<HealthPackageResponse>>> getAllPackages(@RequestParam(required = false) String category,
                                                                                   @RequestParam(required = false) String gender) {

        List<HealthPackageResponse> packages = service.getAllPackages(category, gender);
        return ResponseEntity.ok(ApiResponse.success("Packages fetched successfully", packages));
    }

    //GET package by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HealthPackageResponse>> getPackageById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Package fetched", service.getPackageById(id)));
    }

    //GET popular packages
    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<List<HealthPackageResponse>>> getPopularPackages() {
        return ResponseEntity.ok(
                ApiResponse.success("Popular packages", service.getPopularPackages()));
    }

    //GET all categories
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<String>>> getAllCategories() {
        return ResponseEntity.ok(
                ApiResponse.success("Categories fetched", service.getAllCategories()));
    }

    //Search packages
    // /api/packages/search?query=cancer
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<HealthPackageResponse>>> searchPackages(
            @RequestParam String query) {
        return ResponseEntity.ok(
                ApiResponse.success("Search results", service.searchPackages(query)));
    }

    //CREATE package (ADMIN only)
    @PostMapping
    public ResponseEntity<ApiResponse<HealthPackageResponse>> createPackage(@Valid @RequestBody HealthPackageRequest request) {
        HealthPackageResponse created = service.createPackage(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Package created successfully", created));
    }

    //UPDATE package (ADMIN only)
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<HealthPackageResponse>> updatePackage(@PathVariable Long id,
                                                                            @Valid @RequestBody HealthPackageRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success("Package updated", service.updatePackage(id, request)));
    }

    //SOFT DELETE package (ADMIN only)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePackage(@PathVariable Long id) {
        service.deletePackage(id);
        return ResponseEntity.ok(ApiResponse.success("Package deleted"));
    }
}
