package com.hms.pharmacy.controller;

import com.hms.pharmacy.dto.MedicineDTO;
import com.hms.pharmacy.dto.MedicineInventoryDTO;
import com.hms.pharmacy.repository.MedicineInventoryRepository;
import com.hms.pharmacy.service.MedicineInventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pharmacy/inventory")
@RequiredArgsConstructor
public class MedicineInventoryController {

    private final MedicineInventoryService medicineInventoryService;

    @PostMapping("/add")
    public ResponseEntity<MedicineInventoryDTO> addInventory(@RequestBody MedicineInventoryDTO medicineInventoryDTO){
        return new ResponseEntity<>(medicineInventoryService.addMedicine(medicineInventoryDTO), HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<MedicineInventoryDTO> updateInventory(@RequestBody MedicineInventoryDTO medicineInventoryDTO){
        return new ResponseEntity<>(medicineInventoryService.updateMedicine(medicineInventoryDTO),HttpStatus.OK);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<MedicineInventoryDTO> getInventoryById(@PathVariable Long id){
        return new ResponseEntity<>(medicineInventoryService.getMedicineById(id),HttpStatus.OK);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<MedicineInventoryDTO>> getAllInventory(){
        return new ResponseEntity<>(medicineInventoryService.getAllMedicines(),HttpStatus.OK);
    }
}
