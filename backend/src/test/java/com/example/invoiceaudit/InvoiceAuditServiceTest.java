package com.example.invoiceaudit;

import com.example.invoiceaudit.dto.AuditResultResponse;
import com.example.invoiceaudit.dto.InvoiceAuditRequest;
import com.example.invoiceaudit.dto.InvoiceExtractionResponse;
import com.example.invoiceaudit.entity.AuditLog;
import com.example.invoiceaudit.entity.AuditStatus;
import com.example.invoiceaudit.entity.PurchaseOrder;
import com.example.invoiceaudit.exception.InvalidInvoiceDataException;
import com.example.invoiceaudit.exception.ResourceNotFoundException;
import com.example.invoiceaudit.repository.AuditLogRepository;
import com.example.invoiceaudit.service.AIExtractionService;
import com.example.invoiceaudit.service.InvoiceAuditService;
import com.example.invoiceaudit.service.PurchaseOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoiceAuditServiceTest {

    @Mock
    private PurchaseOrderService purchaseOrderService;

    @Mock
    private AIExtractionService aiExtractionService;

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private InvoiceAuditService invoiceAuditService;

    private PurchaseOrder samplePo;

    @BeforeEach
    void setUp() {
        samplePo = new PurchaseOrder("PO-9921", "Wireless Mouse Pro", 50, new BigDecimal("450.00"));
    }

    @Test
    @DisplayName("Test 1: CLEAR audit when quantity and price match exactly")
    void testClearAudit() {
        InvoiceAuditRequest request = new InvoiceAuditRequest("PO-9921", "Invoice INV-1023 with 50 units @ 450");
        InvoiceExtractionResponse aiExtracted = new InvoiceExtractionResponse(
            "PO-9921", "Wireless Mouse Pro", 50, new BigDecimal("450.00")
        );

        when(purchaseOrderService.findEntityByPoId("PO-9921")).thenReturn(samplePo);
        when(aiExtractionService.extractInvoiceData(request.getInvoiceText())).thenReturn(aiExtracted);
        when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AuditResultResponse result = invoiceAuditService.auditInvoice(request);

        assertNotNull(result);
        assertEquals("CLEAR", result.getStatus());
        assertEquals("CLEAR", result.getComparison().getQuantityStatus());
        assertEquals("CLEAR", result.getComparison().getPriceStatus());
        assertEquals(0, result.getComparison().getQuantityDifference());
        assertEquals(0, result.getComparison().getPriceDifference().compareTo(BigDecimal.ZERO));
        assertTrue(result.getDiscrepancyReason().contains("zero discrepancies"));

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());
        assertEquals(AuditStatus.CLEAR, captor.getValue().getAuditStatus());
    }

    @Test
    @DisplayName("Test 2: Quantity discrepancy when vendor delivers fewer units than expected")
    void testQuantityDiscrepancy() {
        InvoiceAuditRequest request = new InvoiceAuditRequest("PO-9921", "Invoice INV-1025 with 40 units @ 450");
        InvoiceExtractionResponse aiExtracted = new InvoiceExtractionResponse(
            "PO-9921", "Wireless Mouse Pro", 40, new BigDecimal("450.00")
        );

        when(purchaseOrderService.findEntityByPoId("PO-9921")).thenReturn(samplePo);
        when(aiExtractionService.extractInvoiceData(request.getInvoiceText())).thenReturn(aiExtracted);
        when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AuditResultResponse result = invoiceAuditService.auditInvoice(request);

        assertNotNull(result);
        assertEquals("DISCREPANCY", result.getStatus());
        assertEquals("DISCREPANCY", result.getComparison().getQuantityStatus());
        assertEquals("CLEAR", result.getComparison().getPriceStatus());
        assertEquals(-10, result.getComparison().getQuantityDifference());
        assertTrue(result.getDiscrepancyReason().contains("Vendor delivered 40 units while the purchase order expected 50 units."));
    }

    @Test
    @DisplayName("Test 3: Price discrepancy when vendor charges more than agreed unit price")
    void testPriceDiscrepancy() {
        InvoiceAuditRequest request = new InvoiceAuditRequest("PO-9921", "Invoice INV-1024 with 50 units @ 499");
        InvoiceExtractionResponse aiExtracted = new InvoiceExtractionResponse(
            "PO-9921", "Wireless Mouse Pro", 50, new BigDecimal("499.00")
        );

        when(purchaseOrderService.findEntityByPoId("PO-9921")).thenReturn(samplePo);
        when(aiExtractionService.extractInvoiceData(request.getInvoiceText())).thenReturn(aiExtracted);
        when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AuditResultResponse result = invoiceAuditService.auditInvoice(request);

        assertNotNull(result);
        assertEquals("DISCREPANCY", result.getStatus());
        assertEquals("CLEAR", result.getComparison().getQuantityStatus());
        assertEquals("DISCREPANCY", result.getComparison().getPriceStatus());
        assertEquals(new BigDecimal("49.00"), result.getComparison().getPriceDifference());
        assertTrue(result.getDiscrepancyReason().contains("Vendor unit price is ₹499.00 while the agreed purchase-order price is ₹450.00."));
    }

    @Test
    @DisplayName("Test 4: Quantity + Price combined discrepancy")
    void testCombinedDiscrepancy() {
        InvoiceAuditRequest request = new InvoiceAuditRequest("PO-9921", "Invoice INV-1026 with 40 units @ 499");
        InvoiceExtractionResponse aiExtracted = new InvoiceExtractionResponse(
            "PO-9921", "Wireless Mouse Pro", 40, new BigDecimal("499.00")
        );

        when(purchaseOrderService.findEntityByPoId("PO-9921")).thenReturn(samplePo);
        when(aiExtractionService.extractInvoiceData(request.getInvoiceText())).thenReturn(aiExtracted);
        when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AuditResultResponse result = invoiceAuditService.auditInvoice(request);

        assertNotNull(result);
        assertEquals("DISCREPANCY", result.getStatus());
        assertEquals("DISCREPANCY", result.getComparison().getQuantityStatus());
        assertEquals("DISCREPANCY", result.getComparison().getPriceStatus());
        assertTrue(result.getDiscrepancyReason().contains("Quantity discrepancy: vendor delivered 40 units"));
        assertTrue(result.getDiscrepancyReason().contains("Price discrepancy: vendor charged ₹499.00"));
    }

    @Test
    @DisplayName("Test 5: PO not found throws ResourceNotFoundException")
    void testPoNotFound() {
        InvoiceAuditRequest request = new InvoiceAuditRequest("PO-9999", "Invoice INV-9999 content");
        when(purchaseOrderService.findEntityByPoId("PO-9999"))
            .thenThrow(new ResourceNotFoundException("Purchase Order PO-9999 was not found."));

        assertThrows(ResourceNotFoundException.class, () -> invoiceAuditService.auditInvoice(request));
        verify(aiExtractionService, never()).extractInvoiceData(any());
        verify(auditLogRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test 6: Invoice PO ID mismatch throws InvalidInvoiceDataException")
    void testPoIdMismatch() {
        InvoiceAuditRequest request = new InvoiceAuditRequest("PO-9921", "Invoice with PO-9922");
        InvoiceExtractionResponse aiExtracted = new InvoiceExtractionResponse(
            "PO-9922", "Mechanical Keyboard RGB", 20, new BigDecimal("1800.00")
        );

        when(purchaseOrderService.findEntityByPoId("PO-9921")).thenReturn(samplePo);
        when(aiExtractionService.extractInvoiceData(request.getInvoiceText())).thenReturn(aiExtracted);

        InvalidInvoiceDataException ex = assertThrows(
            InvalidInvoiceDataException.class,
            () -> invoiceAuditService.auditInvoice(request)
        );

        assertTrue(ex.getMessage().contains("does not match the submitted PO ID"));
        verify(auditLogRepository, never()).save(any());
    }
}
