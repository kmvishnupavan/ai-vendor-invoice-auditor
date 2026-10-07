package com.example.invoiceaudit.controller;

import com.example.invoiceaudit.dto.PurchaseOrderResponse;
import com.example.invoiceaudit.service.PurchaseOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    public PurchaseOrderController(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    @GetMapping
    public ResponseEntity<List<PurchaseOrderResponse>> getAllPurchaseOrders() {
        return ResponseEntity.ok(purchaseOrderService.getAllPurchaseOrders());
    }

    @GetMapping("/{poId}")
    public ResponseEntity<PurchaseOrderResponse> getPurchaseOrderByPoId(@PathVariable String poId) {
        return ResponseEntity.ok(purchaseOrderService.getPurchaseOrderByPoId(poId));
    }
}
