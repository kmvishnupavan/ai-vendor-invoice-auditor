package com.example.invoiceaudit.service;

import com.example.invoiceaudit.dto.AuditResultResponse;
import com.example.invoiceaudit.dto.InvoiceAuditRequest;
import com.example.invoiceaudit.dto.InvoiceExtractionResponse;
import com.example.invoiceaudit.entity.AuditLog;
import com.example.invoiceaudit.entity.AuditStatus;
import com.example.invoiceaudit.entity.PurchaseOrder;
import com.example.invoiceaudit.exception.InvalidInvoiceDataException;
import com.example.invoiceaudit.repository.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class InvoiceAuditService {

    private static final Logger log = LoggerFactory.getLogger(InvoiceAuditService.class);

    private final PurchaseOrderService purchaseOrderService;
    private final AIExtractionService aiExtractionService;
    private final AuditLogRepository auditLogRepository;

    public InvoiceAuditService(PurchaseOrderService purchaseOrderService,
                               AIExtractionService aiExtractionService,
                               AuditLogRepository auditLogRepository) {
        this.purchaseOrderService = purchaseOrderService;
        this.aiExtractionService = aiExtractionService;
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public AuditResultResponse auditInvoice(InvoiceAuditRequest request) {
        log.info("Processing invoice audit for PO ID: {}", request.getPoId());

        // Step 1: Validate & retrieve Purchase Order from database
        PurchaseOrder po = purchaseOrderService.findEntityByPoId(request.getPoId());

        // Step 2: Call AI extraction service to extract structured invoice data
        InvoiceExtractionResponse extracted = aiExtractionService.extractInvoiceData(request.getInvoiceText());

        // Step 3: Check invoice PO ID against submitted PO ID
        validatePoIdMatch(request.getPoId(), extracted.getPoId());

        // Step 4: Perform Java business validation
        int expectedQty = po.getExpectedQuantity();
        int deliveredQty = extracted.getQuantityDelivered();
        BigDecimal agreedPrice = po.getAgreedUnitPrice();
        BigDecimal chargedPrice = extracted.getUnitPriceCharged();

        // RULE 1: Quantity validation
        boolean quantityDiscrepancy = deliveredQty < expectedQty;
        String quantityStatus = quantityDiscrepancy ? "DISCREPANCY" : "CLEAR";
        int quantityDiff = deliveredQty - expectedQty;

        // RULE 2: Price validation
        boolean priceDiscrepancy = chargedPrice.compareTo(agreedPrice) > 0;
        String priceStatus = priceDiscrepancy ? "DISCREPANCY" : "CLEAR";
        BigDecimal priceDiff = chargedPrice.subtract(agreedPrice);

        // ITEM VALIDATION
        boolean itemDiscrepancy = !isItemNameCompatible(po.getItemName(), extracted.getItemName());
        String itemStatus = itemDiscrepancy ? "DISCREPANCY" : "CLEAR";

        // FINAL STATUS
        boolean hasDiscrepancy = quantityDiscrepancy || priceDiscrepancy || itemDiscrepancy;
        AuditStatus finalStatus = hasDiscrepancy ? AuditStatus.DISCREPANCY : AuditStatus.CLEAR;

        // Generate detailed reason in Java
        String discrepancyReason = buildDiscrepancyReason(
            quantityDiscrepancy, priceDiscrepancy, itemDiscrepancy,
            deliveredQty, expectedQty, chargedPrice, agreedPrice,
            extracted.getItemName(), po.getItemName()
        );

        // Step 5: Save audit record in database
        String logId = generateLogId();
        AuditLog auditLog = new AuditLog(
            logId,
            po.getPoId(),
            finalStatus,
            discrepancyReason,
            extracted.getItemName(),
            deliveredQty,
            chargedPrice
        );
        auditLogRepository.save(auditLog);

        log.info("Audit completed and saved with Log ID: {}, Status: {}", logId, finalStatus);

        // Step 6: Construct and return complete response
        AuditResultResponse.InvoiceDto invoiceDto = new AuditResultResponse.InvoiceDto(
            extracted.getPoId(),
            extracted.getItemName(),
            deliveredQty,
            chargedPrice
        );

        AuditResultResponse.PurchaseOrderDto poDto = new AuditResultResponse.PurchaseOrderDto(
            po.getPoId(),
            po.getItemName(),
            expectedQty,
            agreedPrice
        );

        AuditResultResponse.ComparisonDto comparisonDto = new AuditResultResponse.ComparisonDto(
            quantityStatus,
            priceStatus,
            itemStatus,
            quantityDiff,
            priceDiff
        );

        return new AuditResultResponse(
            logId,
            finalStatus.name(),
            invoiceDto,
            poDto,
            comparisonDto,
            discrepancyReason,
            LocalDateTime.now()
        );
    }

    private void validatePoIdMatch(String submittedPoId, String extractedPoId) {
        if (extractedPoId == null || extractedPoId.isBlank()) {
            throw new InvalidInvoiceDataException("Unable to extract Purchase Order ID from invoice text.");
        }

        String normalizedSubmitted = submittedPoId.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        String normalizedExtracted = extractedPoId.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();

        if (!normalizedSubmitted.equals(normalizedExtracted)) {
            throw new InvalidInvoiceDataException(
                "Invoice PO ID " + extractedPoId.trim() + " does not match the submitted PO ID " + submittedPoId.trim() + "."
            );
        }
    }

    private boolean isItemNameCompatible(String poItem, String invoiceItem) {
        if (poItem == null || invoiceItem == null) return false;
        String p = poItem.trim().toLowerCase();
        String i = invoiceItem.trim().toLowerCase();

        // Exact match or contains
        if (p.equals(i) || p.contains(i) || i.contains(p)) {
            return true;
        }

        // Token match: check if significant words overlap
        String[] poWords = p.replaceAll("[^a-z0-9 ]", "").split("\\s+");
        String[] invWords = i.replaceAll("[^a-z0-9 ]", "").split("\\s+");
        int matches = 0;
        for (String pw : poWords) {
            if (pw.length() > 2) {
                for (String iw : invWords) {
                    if (pw.equals(iw)) {
                        matches++;
                        break;
                    }
                }
            }
        }
        return matches > 0;
    }

    private String buildDiscrepancyReason(boolean qtyDiscrepancy, boolean priceDiscrepancy, boolean itemDiscrepancy,
                                          int deliveredQty, int expectedQty,
                                          BigDecimal chargedPrice, BigDecimal agreedPrice,
                                          String invoiceItem, String poItem) {
        List<String> reasons = new ArrayList<>();

        if (qtyDiscrepancy && priceDiscrepancy) {
            return String.format(
                "1. Quantity discrepancy: vendor delivered %d units while %d units were expected. 2. Price discrepancy: vendor charged ₹%s while the agreed price was ₹%s.",
                deliveredQty, expectedQty, chargedPrice.toPlainString(), agreedPrice.toPlainString()
            );
        }

        if (qtyDiscrepancy) {
            reasons.add(String.format("Vendor delivered %d units while the purchase order expected %d units.", deliveredQty, expectedQty));
        }

        if (priceDiscrepancy) {
            reasons.add(String.format("Vendor unit price is ₹%s while the agreed purchase-order price is ₹%s.", chargedPrice.toPlainString(), agreedPrice.toPlainString()));
        }

        if (itemDiscrepancy) {
            reasons.add(String.format("Item description discrepancy: invoice specifies '%s' while purchase order expected '%s'.", invoiceItem, poItem));
        }

        if (reasons.isEmpty()) {
            return "Invoice matches purchase order specifications with zero discrepancies.";
        }

        return String.join(" ", reasons);
    }

    private String generateLogId() {
        return "AUD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
