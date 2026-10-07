package com.example.invoiceaudit.controller;

import com.example.invoiceaudit.dto.AuditResultResponse;
import com.example.invoiceaudit.dto.InvoiceAuditRequest;
import com.example.invoiceaudit.service.InvoiceAuditService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/audits")
public class InvoiceAuditController {

    private final InvoiceAuditService invoiceAuditService;

    public InvoiceAuditController(InvoiceAuditService invoiceAuditService) {
        this.invoiceAuditService = invoiceAuditService;
    }

    @PostMapping
    public ResponseEntity<AuditResultResponse> auditInvoice(@Valid @RequestBody InvoiceAuditRequest request) {
        AuditResultResponse response = invoiceAuditService.auditInvoice(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
