package com.acme.newsletter.model.dto;

import com.acme.newsletter.model.Subscriber;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO for safely exposing subscriber data.
 * Implemented as a record for immutability and concise syntax.
 */
public record SubscriberResponse(
        UUID id,
        String email,
        boolean active,
        OffsetDateTime subscribedAt
) {
    public static SubscriberResponse fromEntity(Subscriber entity) {
        return new SubscriberResponse(
                entity.getId(),
                entity.getEmail(),
                entity.isActive(),
                entity.getSubscribedAt()
        );
    }
}
