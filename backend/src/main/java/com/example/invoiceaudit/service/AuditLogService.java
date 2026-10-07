package com.example.invoiceaudit.service;

import com.example.invoiceaudit.dto.AuditHistoryItemResponse;
import com.example.invoiceaudit.dto.AuditSummaryResponse;
import com.example.invoiceaudit.entity.AuditLog;
import com.example.invoiceaudit.entity.AuditStatus;
import com.example.invoiceaudit.exception.ResourceNotFoundException;
import com.example.invoiceaudit.repository.AuditLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional(readOnly = true)
    public List<AuditHistoryItemResponse> getRecentAuditHistory() {
        return auditLogRepository.findAllByOrderByAuditedAtDesc().stream()
            .map(this::mapToHistoryItem)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AuditHistoryItemResponse getAuditByLogId(String logId) {
        AuditLog audit = auditLogRepository.findByLogId(logId)
            .orElseThrow(() -> new ResourceNotFoundException("Audit record with Log ID " + logId + " was not found."));
        return mapToHistoryItem(audit);
    }

    @Transactional(readOnly = true)
    public AuditSummaryResponse getAuditSummary() {
        long total = auditLogRepository.count();
        long clearCount = auditLogRepository.countByAuditStatus(AuditStatus.CLEAR);
        long discrepancyCount = auditLogRepository.countByAuditStatus(AuditStatus.DISCREPANCY);
        return new AuditSummaryResponse(total, clearCount, discrepancyCount);
    }

    private AuditHistoryItemResponse mapToHistoryItem(AuditLog log) {
        return new AuditHistoryItemResponse(
            log.getLogId(),
            log.getPoId(),
            log.getAuditStatus(),
            log.getDiscrepancyReason(),
            log.getInvoiceItemName(),
            log.getQuantityDelivered(),
            log.getUnitPriceCharged(),
            log.getAuditedAt()
        );
    }
}
