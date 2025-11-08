package com.acme.newsletter.model.dto;

import java.time.OffsetDateTime;

/**
 * Common response model for API errors.
 * Implemented as a record for immutability and concise syntax.
 */
public record ErrorResponse(
        OffsetDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {}
