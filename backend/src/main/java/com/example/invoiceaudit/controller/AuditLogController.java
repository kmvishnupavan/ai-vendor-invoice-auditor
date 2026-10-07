package com.example.invoiceaudit.controller;

import com.example.invoiceaudit.dto.AuditHistoryItemResponse;
import com.example.invoiceaudit.dto.AuditSummaryResponse;
import com.example.invoiceaudit.service.AuditLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audits")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public ResponseEntity<List<AuditHistoryItemResponse>> getRecentAuditHistory() {
        return ResponseEntity.ok(auditLogService.getRecentAuditHistory());
    }

    @GetMapping("/{logId}")
    public ResponseEntity<AuditHistoryItemResponse> getAuditByLogId(@PathVariable String logId) {
        return ResponseEntity.ok(auditLogService.getAuditByLogId(logId));
    }

    @GetMapping("/summary")
    public ResponseEntity<AuditSummaryResponse> getAuditSummary() {
        return ResponseEntity.ok(auditLogService.getAuditSummary());
    }
}
