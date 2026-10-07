package com.example.invoiceaudit.controller;

import com.example.invoiceaudit.dto.SystemStatusResponse;
import com.example.invoiceaudit.service.AIExtractionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/system")
public class SystemStatusController {

    private final AIExtractionService aiExtractionService;
    private final JdbcTemplate jdbcTemplate;

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    public SystemStatusController(AIExtractionService aiExtractionService, JdbcTemplate jdbcTemplate) {
        this.aiExtractionService = aiExtractionService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/status")
    public ResponseEntity<SystemStatusResponse> getSystemStatus() {
        boolean dbOk = false;
        try {
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            dbOk = (result != null && result == 1);
        } catch (Exception ignored) {
            dbOk = false;
        }

        String dbType = "Relational DB";
        if (datasourceUrl.contains("postgresql")) {
            dbType = "PostgreSQL";
        } else if (datasourceUrl.contains("mysql")) {
            dbType = "MySQL";
        } else if (datasourceUrl.contains("h2")) {
            dbType = "H2 (PostgreSQL Mode)";
        }

        SystemStatusResponse status = new SystemStatusResponse(
            true,
            aiExtractionService.getAiMode(),
            aiExtractionService.getAiModel(),
            dbOk,
            dbType
        );

        return ResponseEntity.ok(status);
    }
}
