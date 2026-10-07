package com.example.invoiceaudit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class InvoiceAuditRequest {

    @NotBlank(message = "Purchase Order ID is required.")
    private String poId;

    @NotBlank(message = "Please paste the vendor invoice text.")
    @Size(min = 10, message = "Invoice text must be at least 10 characters.")
    private String invoiceText;

    public InvoiceAuditRequest() {
    }

    public InvoiceAuditRequest(String poId, String invoiceText) {
        this.poId = poId;
        this.invoiceText = invoiceText;
    }

    public String getPoId() {
        return poId;
    }

    public void setPoId(String poId) {
        this.poId = poId;
    }

    public String getInvoiceText() {
        return invoiceText;
    }

    public void setInvoiceText(String invoiceText) {
        this.invoiceText = invoiceText;
    }
}
