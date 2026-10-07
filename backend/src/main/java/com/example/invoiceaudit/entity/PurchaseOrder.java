package com.example.invoiceaudit.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchase_orders", indexes = {
    @Index(name = "idx_po_id", columnList = "po_id", unique = true)
})
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "po_id", nullable = false, unique = true, length = 50)
    private String poId;

    @Column(name = "item_name", nullable = false, length = 255)
    private String itemName;

    @Column(name = "expected_quantity", nullable = false)
    private Integer expectedQuantity;

    @Column(name = "agreed_unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal agreedUnitPrice;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public PurchaseOrder() {
    }

    public PurchaseOrder(String poId, String itemName, Integer expectedQuantity, BigDecimal agreedUnitPrice) {
        this.poId = poId;
        this.itemName = itemName;
        this.expectedQuantity = expectedQuantity;
        this.agreedUnitPrice = agreedUnitPrice;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPoId() {
        return poId;
    }

    public void setPoId(String poId) {
        this.poId = poId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getExpectedQuantity() {
        return expectedQuantity;
    }

    public void setExpectedQuantity(Integer expectedQuantity) {
        this.expectedQuantity = expectedQuantity;
    }

    public BigDecimal getAgreedUnitPrice() {
        return agreedUnitPrice;
    }

    public void setAgreedUnitPrice(BigDecimal agreedUnitPrice) {
        this.agreedUnitPrice = agreedUnitPrice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
