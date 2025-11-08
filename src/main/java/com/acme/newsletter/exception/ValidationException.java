package com.acme.newsletter.exception;

/**
 * Thrown when business validation fails (e.g., invalid date/time, inactive subscriber).
 */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
