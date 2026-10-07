package com.example.invoiceaudit.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PurchaseOrderResponse {

    private String poId;
    private String itemName;
    private Integer expectedQuantity;
    private BigDecimal agreedUnitPrice;
    private LocalDateTime createdAt;

    public PurchaseOrderResponse() {
    }

    public PurchaseOrderResponse(String poId, String itemName, Integer expectedQuantity, BigDecimal agreedUnitPrice, LocalDateTime createdAt) {
        this.poId = poId;
        this.itemName = itemName;
        this.expectedQuantity = expectedQuantity;
        this.agreedUnitPrice = agreedUnitPrice;
        this.createdAt = createdAt;
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
