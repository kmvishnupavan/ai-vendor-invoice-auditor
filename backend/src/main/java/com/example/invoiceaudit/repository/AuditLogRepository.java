package com.example.invoiceaudit.repository;

import com.example.invoiceaudit.entity.AuditLog;
import com.example.invoiceaudit.entity.AuditStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    Optional<AuditLog> findByLogId(String logId);
    List<AuditLog> findAllByOrderByAuditedAtDesc();
    long countByAuditStatus(AuditStatus auditStatus);
}
