package com.hms.pharmacy.service.serviceImp;

import com.hms.pharmacy.dto.SaleItemDTO;
import com.hms.pharmacy.entity.SaleItem;
import com.hms.pharmacy.exception.HMSException;
import com.hms.pharmacy.repository.SaleItemRepository;
import com.hms.pharmacy.service.SaleItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleItemServiceImp implements SaleItemService {

    private final SaleItemRepository saleItemRepository;

    @Override
    public Long createSaleItem(SaleItemDTO saleItemDTO) {
        return saleItemRepository.save(saleItemDTO.toEntity()).getId();
    }

    @Override
    public void createMultipleSaleItem(Long saleId, Long medicineId, List<SaleItemDTO> saleItemDTOs) {
        saleItemDTOs.stream().map((x) -> {
            x.setSaleId(saleId);
            x.setMedicineId(medicineId);
            return x.toEntity();
        }).forEach(saleItemRepository::save);
    }

    @Override
    public void updateSaleItem(SaleItemDTO saleItemDTO) {
        SaleItem existingSaleItem =
                saleItemRepository.findById(saleItemDTO.getId())
                        .orElseThrow(() -> new
                                HMSException("SALE_ITEM_NOT_FOUND"));
        existingSaleItem.setQuantity(saleItemDTO.getQuantity());
        existingSaleItem.setUnitPrice(saleItemDTO.getUnitPrice());
        saleItemRepository.save(existingSaleItem);
    }

    @Override
    public List<SaleItemDTO> getSaleItemsBySaleId(Long saleId) {
        return saleItemRepository.findBySaleId(saleId).stream()
                .map(SaleItem::toDTO)
                .toList();
    }

    @Override
    public SaleItemDTO getSaleItem(Long id) {
        return saleItemRepository.findById(id)
                .map(SaleItem::toDTO)
                .orElseThrow(() -> new HMSException("SALE_ITEM_NOT_FOUND"));
    }
}
