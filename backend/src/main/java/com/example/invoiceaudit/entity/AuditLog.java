package com.example.invoiceaudit.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs", indexes = {
    @Index(name = "idx_log_id", columnList = "log_id", unique = true),
    @Index(name = "idx_audit_po_id", columnList = "po_id")
})
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "log_id", nullable = false, unique = true, length = 64)
    private String logId;

    @Column(name = "po_id", nullable = false, length = 50)
    private String poId;

    @Enumerated(EnumType.STRING)
    @Column(name = "audit_status", nullable = false, length = 20)
    private AuditStatus auditStatus;

    @Column(name = "discrepancy_reason", columnDefinition = "TEXT")
    private String discrepancyReason;

    @Column(name = "invoice_item_name", length = 255)
    private String invoiceItemName;

    @Column(name = "quantity_delivered")
    private Integer quantityDelivered;

    @Column(name = "unit_price_charged", precision = 12, scale = 2)
    private BigDecimal unitPriceCharged;

    @Column(name = "audited_at", nullable = false)
    private LocalDateTime auditedAt;

    public AuditLog() {
    }

    public AuditLog(String logId, String poId, AuditStatus auditStatus, String discrepancyReason,
                    String invoiceItemName, Integer quantityDelivered, BigDecimal unitPriceCharged) {
        this.logId = logId;
        this.poId = poId;
        this.auditStatus = auditStatus;
        this.discrepancyReason = discrepancyReason;
        this.invoiceItemName = invoiceItemName;
        this.quantityDelivered = quantityDelivered;
        this.unitPriceCharged = unitPriceCharged;
        this.auditedAt = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        if (this.auditedAt == null) {
            this.auditedAt = LocalDateTime.now();
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getPoId() {
        return poId;
    }

    public void setPoId(String poId) {
        this.poId = poId;
    }

    public AuditStatus getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(AuditStatus auditStatus) {
        this.auditStatus = auditStatus;
    }

    public String getDiscrepancyReason() {
        return discrepancyReason;
    }

    public void setDiscrepancyReason(String discrepancyReason) {
        this.discrepancyReason = discrepancyReason;
    }

    public String getInvoiceItemName() {
        return invoiceItemName;
    }

    public void setInvoiceItemName(String invoiceItemName) {
        this.invoiceItemName = invoiceItemName;
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

    public LocalDateTime getAuditedAt() {
        return auditedAt;
    }

    public void setAuditedAt(LocalDateTime auditedAt) {
        this.auditedAt = auditedAt;
    }
}
