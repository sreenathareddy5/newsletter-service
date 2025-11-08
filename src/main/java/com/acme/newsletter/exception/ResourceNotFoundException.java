package com.acme.newsletter.exception;

/**
 * Thrown when a requested resource (e.g., Topic, Content, Subscriber) is not found.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
