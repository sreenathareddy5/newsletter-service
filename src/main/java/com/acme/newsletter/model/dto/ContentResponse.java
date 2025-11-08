package com.acme.newsletter.model.dto;

import com.acme.newsletter.model.Content;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO for returning newsletter content information safely via API.
 * Implemented as a record for immutability and simplicity.
 */
public record ContentResponse(
        UUID id,
        String title,
        String body,
        OffsetDateTime scheduledTime,
        String status,
        UUID topicId,
        String topicName
) {
    public static ContentResponse fromEntity(Content entity) {
        return new ContentResponse(
                entity.getId(),
                entity.getTitle(),
                entity.getBody(),
                entity.getScheduledTime(),
                entity.getStatus().name(),
                entity.getTopic() != null ? entity.getTopic().getId() : null,
                entity.getTopic() != null ? entity.getTopic().getName() : null
        );
    }
}
