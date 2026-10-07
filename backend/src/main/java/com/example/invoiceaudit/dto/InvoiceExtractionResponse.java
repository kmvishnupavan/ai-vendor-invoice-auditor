package com.example.invoiceaudit.dto;

import java.math.BigDecimal;

public class InvoiceExtractionResponse {

    private String poId;
    private String itemName;
    private Integer quantityDelivered;
    private BigDecimal unitPriceCharged;

    public InvoiceExtractionResponse() {
    }

    public InvoiceExtractionResponse(String poId, String itemName, Integer quantityDelivered, BigDecimal unitPriceCharged) {
        this.poId = poId;
        this.itemName = itemName;
        this.quantityDelivered = quantityDelivered;
        this.unitPriceCharged = unitPriceCharged;
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

    public Integer getQuantityDelivered() {
        return quantityDelivered;
    }

    public void setQuantityDelivered(Integer quantityDelivered) {
        this.quantityDelivered = quantityDelivered;
    }

    public BigDecimal getUnitPriceCharged() {
        return unitPriceCharged;
    }

    public void setUnitPriceCharged(BigDecimal unitPriceCharged) {
        this.unitPriceCharged = unitPriceCharged;
    }
}
