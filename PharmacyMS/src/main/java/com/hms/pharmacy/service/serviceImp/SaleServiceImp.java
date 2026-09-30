package com.hms.pharmacy.service.serviceImp;

import com.hms.pharmacy.dto.SaleDTO;
import com.hms.pharmacy.entity.Sale;
import com.hms.pharmacy.exception.HMSException;
import com.hms.pharmacy.repository.SaleRepository;
import com.hms.pharmacy.service.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SaleServiceImp implements SaleService {

    private final SaleRepository saleRepository;

    @Override
    public Long createSale(SaleDTO dto) {
        if (saleRepository.existsByPrescriptionId(dto.getPrescriptionId())) {
            throw new HMSException("SALE_ALREADY_EXISTS");
        }
        dto.setSaleDate(LocalDateTime.now());
        return saleRepository.save(dto.toEntity()).getId();
    }

    @Override
    public void updateSale(SaleDTO dto) {
        Sale sale = saleRepository.findById(dto.getId())
                .orElseThrow(() -> new HMSException("SALE_NOT_FOUND"));
        sale.setSaleDate(dto.getSaleDate());
        sale.setTotalAmount(dto.getTotalAmount());
        saleRepository.save(sale);
    }

    @Override
    public SaleDTO getSale(Long id) {
        return saleRepository.findById(id)
                .orElseThrow(()->new HMSException("SALE_NOT_FOUND")).toDTO();
    }

    @Override
    public SaleDTO getSaleByPrescriptionId(Long prescriptionId) {
        return saleRepository.findByPrescriptionId(prescriptionId)
                .orElseThrow(()->new HMSException("SALE_NOT_FOUND")).toDTO();
    }
}
