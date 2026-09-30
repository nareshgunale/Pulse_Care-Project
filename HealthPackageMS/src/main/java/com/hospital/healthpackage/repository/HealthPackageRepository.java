package com.hospital.healthpackage.repository;

import com.hospital.healthpackage.entity.HealthPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HealthPackageRepository extends JpaRepository<HealthPackage, Long> {

    // Filter by category
    List<HealthPackage> findByCategoryIgnoreCaseAndIsActiveTrue(String category);

    // Filter by gender
    List<HealthPackage> findByIdealForInAndIsActiveTrue(List<String> genders);

    // Filter by category + gender
    List<HealthPackage> findByCategoryIgnoreCaseAndIdealForInAndIsActiveTrue(
            String category, List<String> genders);

    // All active packages
    List<HealthPackage> findByIsActiveTrue();

    // Popular packages
    List<HealthPackage> findByIsPopularTrueAndIsActiveTrue();

    // All distinct categories
    @Query("SELECT DISTINCT h.category FROM HealthPackage h WHERE h.isActive = true")
    List<String> findAllActiveCategories();

    // Search by name
    List<HealthPackage> findByNameContainingIgnoreCaseAndIsActiveTrue(String name);
}
