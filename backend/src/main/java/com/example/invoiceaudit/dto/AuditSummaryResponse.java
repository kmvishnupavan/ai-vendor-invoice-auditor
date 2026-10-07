package com.example.invoiceaudit.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class AuditSummaryResponse {

    private long totalAudits;
    private long clearAudits;
    private long discrepancies;
    private double discrepancyRate;

    public AuditSummaryResponse() {
    }

    public AuditSummaryResponse(long totalAudits, long clearAudits, long discrepancies) {
        this.totalAudits = totalAudits;
        this.clearAudits = clearAudits;
        this.discrepancies = discrepancies;
        if (totalAudits > 0) {
            BigDecimal rate = BigDecimal.valueOf((double) discrepancies * 100 / totalAudits)
                .setScale(1, RoundingMode.HALF_UP);
            this.discrepancyRate = rate.doubleValue();
        } else {
            this.discrepancyRate = 0.0;
        }
    }

    public long getTotalAudits() {
        return totalAudits;
    }

    public void setTotalAudits(long totalAudits) {
        this.totalAudits = totalAudits;
    }

    public long getClearAudits() {
        return clearAudits;
    }

    public void setClearAudits(long clearAudits) {
        this.clearAudits = clearAudits;
    }

    public long getDiscrepancies() {
        return discrepancies;
    }

    public void setDiscrepancies(long discrepancies) {
        this.discrepancies = discrepancies;
    }

    public double getDiscrepancyRate() {
        return discrepancyRate;
    }

    public void setDiscrepancyRate(double discrepancyRate) {
        this.discrepancyRate = discrepancyRate;
    }
}
