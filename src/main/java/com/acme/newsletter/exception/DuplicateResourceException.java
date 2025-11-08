package com.acme.newsletter.exception;

/**
 * Thrown when trying to create a duplicate entity (e.g., topic name already exists, content already scheduled).
 */
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
