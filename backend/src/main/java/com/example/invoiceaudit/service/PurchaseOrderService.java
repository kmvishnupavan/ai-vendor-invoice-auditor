package com.example.invoiceaudit.service;

import com.example.invoiceaudit.dto.PurchaseOrderResponse;
import com.example.invoiceaudit.entity.PurchaseOrder;
import com.example.invoiceaudit.exception.ResourceNotFoundException;
import com.example.invoiceaudit.repository.PurchaseOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;

    public PurchaseOrderService(PurchaseOrderRepository purchaseOrderRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
    }

    @Transactional(readOnly = true)
    public List<PurchaseOrderResponse> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PurchaseOrderResponse getPurchaseOrderByPoId(String poId) {
        PurchaseOrder po = findEntityByPoId(poId);
        return mapToResponse(po);
    }

    @Transactional(readOnly = true)
    public PurchaseOrder findEntityByPoId(String poId) {
        if (poId == null || poId.isBlank()) {
            throw new IllegalArgumentException("Purchase Order ID cannot be empty.");
        }
        return purchaseOrderRepository.findByPoIdIgnoreCase(poId.trim())
            .orElseThrow(() -> new ResourceNotFoundException("Purchase Order " + poId.trim() + " was not found."));
    }

    private PurchaseOrderResponse mapToResponse(PurchaseOrder po) {
        return new PurchaseOrderResponse(
            po.getPoId(),
            po.getItemName(),
            po.getExpectedQuantity(),
            po.getAgreedUnitPrice(),
            po.getCreatedAt()
        );
    }
}
