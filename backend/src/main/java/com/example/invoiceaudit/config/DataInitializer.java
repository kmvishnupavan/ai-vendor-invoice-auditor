package com.example.invoiceaudit.config;

import com.example.invoiceaudit.entity.PurchaseOrder;
import com.example.invoiceaudit.repository.PurchaseOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final PurchaseOrderRepository purchaseOrderRepository;

    public DataInitializer(PurchaseOrderRepository purchaseOrderRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
    }

    @Override
    public void run(String... args) {
        seedPurchaseOrders();
    }

    private void seedPurchaseOrders() {
        List<PurchaseOrder> sampleOrders = List.of(
            new PurchaseOrder("PO-9921", "Wireless Mouse Pro", 50, new BigDecimal("450.00")),
            new PurchaseOrder("PO-9922", "Mechanical Keyboard RGB", 20, new BigDecimal("1800.00")),
            new PurchaseOrder("PO-9923", "Type-C USB Hub 6-in-1", 100, new BigDecimal("350.00")),
            new PurchaseOrder("PO-9924", "UltraWide 4K Gaming Monitor", 15, new BigDecimal("28500.00")),
            new PurchaseOrder("PO-9925", "Ergonomic Desk Chair", 30, new BigDecimal("7200.00"))
        );

        for (PurchaseOrder po : sampleOrders) {
            if (!purchaseOrderRepository.existsByPoId(po.getPoId())) {
                purchaseOrderRepository.save(po);
                log.info("Seeded Purchase Order: {} - {}", po.getPoId(), po.getItemName());
            }
        }

        log.info("Purchase Order verification complete. Total POs in DB: {}", purchaseOrderRepository.count());
    }
}
