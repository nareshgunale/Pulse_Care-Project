package com.hms.pharmacy.repository;

import com.hms.pharmacy.entity.MedicineInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MedicineInventoryRepository extends JpaRepository<MedicineInventory ,Long> {

    List<MedicineInventory> findByExpireDateBefore(LocalDate date);
}
