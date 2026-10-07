package com.example.invoiceaudit.service;

import com.example.invoiceaudit.dto.InvoiceExtractionResponse;
import com.example.invoiceaudit.exception.AIExtractionException;
import com.example.invoiceaudit.exception.InvalidInvoiceDataException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AIExtractionService {

    private static final Logger log = LoggerFactory.getLogger(AIExtractionService.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${invoiceguard.ai.api-key:}")
    private String apiKey;

    @Value("${invoiceguard.ai.api-url:https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent}")
    private String apiUrl;

    @Value("${invoiceguard.ai.model:gemini-1.5-flash}")
    private String model;

    public AIExtractionService(RestTemplateBuilder restTemplateBuilder, ObjectMapper objectMapper,
                               @Value("${invoiceguard.ai.timeout-ms:15000}") int timeoutMs) {
        this.restTemplate = restTemplateBuilder
            .setConnectTimeout(Duration.ofMillis(timeoutMs))
            .setReadTimeout(Duration.ofMillis(timeoutMs))
            .build();
        this.objectMapper = objectMapper;
    }

    public boolean isLiveAiConfigured() {
        return apiKey != null && !apiKey.isBlank() && !apiKey.equalsIgnoreCase("MOCK_MODE") && !apiKey.startsWith("YOUR_");
    }

    public String getAiMode() {
        return isLiveAiConfigured() ? "GEMINI_LIVE" : "LOCAL_INTELLIGENT_EXTRACTOR";
    }

    public String getAiModel() {
        return model;
    }

    /**
     * Extracts structured invoice details using Gemini API if API key is provided,
     * or using local deterministic invoice extraction engine for offline/test environments.
     */
    public InvoiceExtractionResponse extractInvoiceData(String invoiceText) {
        if (invoiceText == null || invoiceText.trim().isEmpty()) {
            throw new InvalidInvoiceDataException("Invoice text cannot be empty.");
        }

        if (isLiveAiConfigured()) {
            return extractUsingGeminiApi(invoiceText);
        } else {
            log.info("AI API key not configured or in test mode. Using local intelligent extraction engine.");
            return extractUsingLocalEngine(invoiceText);
        }
    }

    private InvoiceExtractionResponse extractUsingGeminiApi(String invoiceText) {
        log.info("Invoking Gemini API for invoice extraction (Model: {})", model);

        String systemPrompt = """
            You are an invoice information extraction system.
            Extract only the following fields from the invoice text:
            - poId: String (the purchase order identifier, e.g. PO-9921)
            - itemName: String (description or name of the supplied item)
            - quantityDelivered: Integer (number of units delivered or supplied)
            - unitPriceCharged: Number (charged unit price per item, excluding currency symbol)
            
            Return ONLY valid JSON matching this structure:
            {
              "poId": "PO-9921",
              "itemName": "Wireless Mouse Pro",
              "quantityDelivered": 50,
              "unitPriceCharged": 499
            }
            
            CRITICAL RULES:
            - Return ONLY valid JSON.
            - Do not include Markdown blocks like ```json or ```.
            - Do not explain the answer.
            - Do not calculate discrepancies.
            - Do not compare against purchase orders.
            - Do not make business decisions.
            - If a value cannot be found, return null.
            """;

        String fullPrompt = systemPrompt + "\n\nInvoice text:\n\"\"\"\n" + invoiceText + "\n\"\"\"";

        try {
            Map<String, Object> textPart = Map.of("text", fullPrompt);
            Map<String, Object> content = Map.of("parts", List.of(textPart));

            Map<String, Object> generationConfig = new HashMap<>();
            generationConfig.put("temperature", 0.0);
            generationConfig.put("responseMimeType", "application/json");

            Map<String, Object> requestPayload = new HashMap<>();
            requestPayload.put("contents", List.of(content));
            requestPayload.put("generationConfig", generationConfig);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestPayload, headers);

            String requestUrl = apiUrl.contains("key=") ? apiUrl : apiUrl + "?key=" + apiKey;

            ResponseEntity<String> response = restTemplate.postForEntity(requestUrl, entity, String.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new AIExtractionException("Gemini API returned unexpected status: " + response.getStatusCode());
            }

            return parseGeminiResponse(response.getBody());

        } catch (HttpClientErrorException.Forbidden | HttpClientErrorException.Unauthorized e) {
            log.error("Gemini API authentication failed: {}", e.getMessage());
            throw new AIExtractionException("Invalid or unauthorized AI API key. Please check your AI_API_KEY environment variable.");
        } catch (HttpClientErrorException.TooManyRequests e) {
            log.error("Gemini API quota exceeded: {}", e.getMessage());
            throw new AIExtractionException("AI API quota exceeded. Please check your Gemini rate limits or quota.");
        } catch (ResourceAccessException e) {
            log.error("Gemini API timeout or network failure: {}", e.getMessage());
            throw new AIExtractionException("AI extraction request timed out. Please check network connectivity.");
        } catch (AIExtractionException e) {
            throw e;
        } catch (Exception e) {
            log.error("Gemini API extraction failed: {}", e.getMessage(), e);
            throw new AIExtractionException("AI extraction service error: " + e.getMessage(), e);
        }
    }

    private InvoiceExtractionResponse parseGeminiResponse(String responseJson) {
        try {
            JsonNode rootNode = objectMapper.readTree(responseJson);
            JsonNode textNode = rootNode.at("/candidates/0/content/parts/0/text");

            if (textNode.isMissingNode() || textNode.asText().isBlank()) {
                throw new AIExtractionException("Empty response content from Gemini API.");
            }

            String rawText = textNode.asText().trim();
            // Sanitize in case model included markdown fences despite instructions
            rawText = rawText.replaceAll("^```json\\s*", "").replaceAll("^```\\s*", "").replaceAll("```$", "").trim();

            JsonNode extractionNode = objectMapper.readTree(rawText);

            String poId = extractionNode.hasNonNull("poId") ? extractionNode.get("poId").asText().trim() : null;
            String itemName = extractionNode.hasNonNull("itemName") ? extractionNode.get("itemName").asText().trim() : null;

            Integer quantity = null;
            if (extractionNode.hasNonNull("quantityDelivered")) {
                quantity = extractionNode.get("quantityDelivered").asInt();
            }

            BigDecimal unitPrice = null;
            if (extractionNode.hasNonNull("unitPriceCharged")) {
                unitPrice = new BigDecimal(extractionNode.get("unitPriceCharged").asText().replaceAll("[^0-9.]", ""));
            }

            return validateAndBuildExtraction(poId, itemName, quantity, unitPrice);

        } catch (AIExtractionException | InvalidInvoiceDataException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to parse AI response JSON: {}", e.getMessage(), e);
            throw new AIExtractionException("Invalid response format received from AI model: " + e.getMessage(), e);
        }
    }

    /**
     * High-precision local fallback engine for offline development, local unit testing,
     * and demo evaluation without requiring an active external API billing key.
     */
    public InvoiceExtractionResponse extractUsingLocalEngine(String invoiceText) {
        String poId = extractPattern(invoiceText, "(?i)(?:Purchase\\s*Order|PO\\s*(?:Number|ID|#)?|P\\.?O\\.?)\\s*[:=]?\\s*(PO[-_]?[0-9]+)");
        if (poId == null) {
            poId = extractPattern(invoiceText, "(?i)\\b(PO[-_]?[0-9]{4,})\\b");
        }

        String itemName = extractPattern(invoiceText, "(?i)(?:Item(?:\\s*Name|\\s*Description)?|Product|Description)\\s*[:=]\\s*([^\\r\\n,;]+)");
        if (itemName != null) {
            itemName = itemName.trim();
        }

        Integer quantity = null;
        String qtyStr = extractPattern(invoiceText, "(?i)(?:Quantity(?:\\s*Supplied|\\s*Delivered|\\s*Shipped)?|Qty(?:\\s*Delivered|\\s*Supplied)?|Units)\\s*[:=]?\\s*([0-9]+)");
        if (qtyStr != null) {
            try {
                quantity = Integer.parseInt(qtyStr.trim());
            } catch (NumberFormatException ignored) {}
        }

        BigDecimal unitPrice = null;
        String priceStr = extractPattern(invoiceText, "(?i)(?:Unit\\s*Price(?:\\s*Charged)?|Price(?:\\s*per\\s*unit)?|Rate)\\s*[:=]?\\s*[₹$€£]?\\s*([0-9]+(?:\\.[0-9]{1,2})?)");
        if (priceStr != null) {
            try {
                unitPrice = new BigDecimal(priceStr.trim());
            } catch (Exception ignored) {}
        }

        return validateAndBuildExtraction(poId, itemName, quantity, unitPrice);
    }

    private InvoiceExtractionResponse validateAndBuildExtraction(String poId, String itemName, Integer quantity, BigDecimal unitPrice) {
        List<String> missingFields = new ArrayList<>();
        if (poId == null || poId.isBlank()) missingFields.add("poId");
        if (itemName == null || itemName.isBlank()) missingFields.add("itemName");
        if (quantity == null) missingFields.add("quantityDelivered");
        if (unitPrice == null) missingFields.add("unitPriceCharged");

        if (!missingFields.isEmpty()) {
            throw new InvalidInvoiceDataException(
                "Unable to extract required invoice fields: " + String.join(", ", missingFields) + ". Please verify invoice format."
            );
        }

        if (quantity <= 0) {
            throw new InvalidInvoiceDataException("Invalid extracted quantity (" + quantity + "): must be greater than zero.");
        }

        if (unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidInvoiceDataException("Invalid extracted price (" + unitPrice + "): must be greater than zero.");
        }

        return new InvoiceExtractionResponse(poId, itemName, quantity, unitPrice);
    }

    private String extractPattern(String text, String regex) {
        Matcher matcher = Pattern.compile(regex).matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }
}
