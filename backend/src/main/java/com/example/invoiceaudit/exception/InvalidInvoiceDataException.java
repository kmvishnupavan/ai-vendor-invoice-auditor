package com.example.invoiceaudit.exception;

public class InvalidInvoiceDataException extends RuntimeException {
    public InvalidInvoiceDataException(String message) {
        super(message);
    }
}
