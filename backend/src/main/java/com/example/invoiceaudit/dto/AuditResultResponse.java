package com.example.invoiceaudit.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AuditResultResponse {

    private String logId;
    private String status;
    private InvoiceDto invoice;
    private PurchaseOrderDto purchaseOrder;
    private ComparisonDto comparison;
    private String discrepancyReason;
    private LocalDateTime auditedAt;

    public AuditResultResponse() {
    }

    public AuditResultResponse(String logId, String status, InvoiceDto invoice,
                               PurchaseOrderDto purchaseOrder, ComparisonDto comparison,
                               String discrepancyReason, LocalDateTime auditedAt) {
        this.logId = logId;
        this.status = status;
        this.invoice = invoice;
        this.purchaseOrder = purchaseOrder;
        this.comparison = comparison;
        this.discrepancyReason = discrepancyReason;
        this.auditedAt = auditedAt;
    }

    // Inner DTOs for structured representation
    public static class InvoiceDto {
        private String poId;
        private String itemName;
        private Integer quantityDelivered;
        private BigDecimal unitPriceCharged;

        public InvoiceDto() {}

        public InvoiceDto(String poId, String itemName, Integer quantityDelivered, BigDecimal unitPriceCharged) {
            this.poId = poId;
            this.itemName = itemName;
            this.quantityDelivered = quantityDelivered;
            this.unitPriceCharged = unitPriceCharged;
        }

        public String getPoId() { return poId; }
        public void setPoId(String poId) { this.poId = poId; }
        public String getItemName() { return itemName; }
        public void setItemName(String itemName) { this.itemName = itemName; }
        public Integer getQuantityDelivered() { return quantityDelivered; }
        public void setQuantityDelivered(Integer quantityDelivered) { this.quantityDelivered = quantityDelivered; }
        public BigDecimal getUnitPriceCharged() { return unitPriceCharged; }
        public void setUnitPriceCharged(BigDecimal unitPriceCharged) { this.unitPriceCharged = unitPriceCharged; }
    }

    public static class PurchaseOrderDto {
        private String poId;
        private String itemName;
        private Integer expectedQuantity;
        private BigDecimal agreedUnitPrice;

        public PurchaseOrderDto() {}

        public PurchaseOrderDto(String poId, String itemName, Integer expectedQuantity, BigDecimal agreedUnitPrice) {
            this.poId = poId;
            this.itemName = itemName;
            this.expectedQuantity = expectedQuantity;
            this.agreedUnitPrice = agreedUnitPrice;
        }

        public String getPoId() { return poId; }
        public void setPoId(String poId) { this.poId = poId; }
        public String getItemName() { return itemName; }
        public void setItemName(String itemName) { this.itemName = itemName; }
        public Integer getExpectedQuantity() { return expectedQuantity; }
        public void setExpectedQuantity(Integer expectedQuantity) { this.expectedQuantity = expectedQuantity; }
        public BigDecimal getAgreedUnitPrice() { return agreedUnitPrice; }
        public void setAgreedUnitPrice(BigDecimal agreedUnitPrice) { this.agreedUnitPrice = agreedUnitPrice; }
    }

    public static class ComparisonDto {
        private String quantityStatus;
        private String priceStatus;
        private String itemStatus;
        private Integer quantityDifference;
        private BigDecimal priceDifference;

        public ComparisonDto() {}

        public ComparisonDto(String quantityStatus, String priceStatus, String itemStatus,
                             Integer quantityDifference, BigDecimal priceDifference) {
            this.quantityStatus = quantityStatus;
            this.priceStatus = priceStatus;
            this.itemStatus = itemStatus;
            this.quantityDifference = quantityDifference;
            this.priceDifference = priceDifference;
        }

        public String getQuantityStatus() { return quantityStatus; }
        public void setQuantityStatus(String quantityStatus) { this.quantityStatus = quantityStatus; }
        public String getPriceStatus() { return priceStatus; }
        public void setPriceStatus(String priceStatus) { this.priceStatus = priceStatus; }
        public String getItemStatus() { return itemStatus; }
        public void setItemStatus(String itemStatus) { this.itemStatus = itemStatus; }
        public Integer getQuantityDifference() { return quantityDifference; }
        public void setQuantityDifference(Integer quantityDifference) { this.quantityDifference = quantityDifference; }
        public BigDecimal getPriceDifference() { return priceDifference; }
        public void setPriceDifference(BigDecimal priceDifference) { this.priceDifference = priceDifference; }
    }

    // Getters and Setters
    public String getLogId() { return logId; }
    public void setLogId(String logId) { this.logId = logId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public InvoiceDto getInvoice() { return invoice; }
    public void setInvoice(InvoiceDto invoice) { this.invoice = invoice; }
    public PurchaseOrderDto getPurchaseOrder() { return purchaseOrder; }
    public void setPurchaseOrder(PurchaseOrderDto purchaseOrder) { this.purchaseOrder = purchaseOrder; }
    public ComparisonDto getComparison() { return comparison; }
    public void setComparison(ComparisonDto comparison) { this.comparison = comparison; }
    public String getDiscrepancyReason() { return discrepancyReason; }
    public void setDiscrepancyReason(String discrepancyReason) { this.discrepancyReason = discrepancyReason; }
    public LocalDateTime getAuditedAt() { return auditedAt; }
    public void setAuditedAt(LocalDateTime auditedAt) { this.auditedAt = auditedAt; }
}
