package com.example.invoiceaudit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@SpringBootApplication
public class InvoiceAuditApplication {

    private static final Logger log = LoggerFactory.getLogger(InvoiceAuditApplication.class);

    public static void main(String[] args) {
        loadDotEnv();
        SpringApplication.run(InvoiceAuditApplication.class, args);
    }

    private static void loadDotEnv() {
        for (String filename : List.of(".env", "backend/.env", "../.env")) {
            Path path = Path.of(filename);
            if (Files.exists(path)) {
                try {
                    List<String> lines = Files.readAllLines(path);
                    for (String line : lines) {
                        line = line.trim();
                        if (line.isEmpty() || line.startsWith("#") || !line.contains("=")) {
                            continue;
                        }
                        int eqIdx = line.indexOf('=');
                        String key = line.substring(0, eqIdx).trim();
                        String value = line.substring(eqIdx + 1).trim();
                        if ((value.startsWith("\"") && value.endsWith("\"")) ||
                            (value.startsWith("'") && value.endsWith("'"))) {
                            value = value.substring(1, value.length() - 1);
                        }
                        if (System.getProperty(key) == null && System.getenv(key) == null && !value.isEmpty()) {
                            System.setProperty(key, value);
                            log.info("Loaded property from .env: {}", key);
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }
}
