package com.example.invoiceaudit.dto;

import com.example.invoiceaudit.entity.AuditStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AuditHistoryItemResponse {

    private String logId;
    private String poId;
    private AuditStatus status;
    private String discrepancyReason;
    private String invoiceItemName;
    private Integer quantityDelivered;
    private BigDecimal unitPriceCharged;
    private LocalDateTime auditedAt;

    public AuditHistoryItemResponse() {
    }

    public AuditHistoryItemResponse(String logId, String poId, AuditStatus status, String discrepancyReason,
                                    String invoiceItemName, Integer quantityDelivered,
                                    BigDecimal unitPriceCharged, LocalDateTime auditedAt) {
        this.logId = logId;
        this.poId = poId;
        this.status = status;
        this.discrepancyReason = discrepancyReason;
        this.invoiceItemName = invoiceItemName;
        this.quantityDelivered = quantityDelivered;
        this.unitPriceCharged = unitPriceCharged;
        this.auditedAt = auditedAt;
    }

    public String getLogId() { return logId; }
    public void setLogId(String logId) { this.logId = logId; }
    public String getPoId() { return poId; }
    public void setPoId(String poId) { this.poId = poId; }
    public AuditStatus getStatus() { return status; }
    public void setStatus(AuditStatus status) { this.status = status; }
    public String getDiscrepancyReason() { return discrepancyReason; }
    public void setDiscrepancyReason(String discrepancyReason) { this.discrepancyReason = discrepancyReason; }
    public String getInvoiceItemName() { return invoiceItemName; }
    public void setInvoiceItemName(String invoiceItemName) { this.invoiceItemName = invoiceItemName; }
    public Integer getQuantityDelivered() { return quantityDelivered; }
    public void setQuantityDelivered(Integer quantityDelivered) { this.quantityDelivered = quantityDelivered; }
    public BigDecimal getUnitPriceCharged() { return unitPriceCharged; }
    public void setUnitPriceCharged(BigDecimal unitPriceCharged) { this.unitPriceCharged = unitPriceCharged; }
    public LocalDateTime getAuditedAt() { return auditedAt; }
    public void setAuditedAt(LocalDateTime auditedAt) { this.auditedAt = auditedAt; }
}
