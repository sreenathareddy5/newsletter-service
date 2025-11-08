package com.acme.newsletter.model.dto;

import com.acme.newsletter.model.Subscription;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO for subscription details (link between subscriber and topic).
 * Implemented as a record for immutability and simplicity.
 */
public record SubscriptionResponse(
        UUID subscriberId,
        UUID topicId,
        String topicName,
        OffsetDateTime subscribedAt
) {
    public static SubscriptionResponse fromEntity(Subscription sub) {
        return new SubscriptionResponse(
                sub.getSubscriber().getId(),
                sub.getTopic().getId(),
                sub.getTopic().getName(),
                sub.getSubscribedAt()
        );
    }
}
