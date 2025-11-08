package com.acme.newsletter.model.dto;

import com.acme.newsletter.model.Topic;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO for safely exposing Topic data.
 * Implemented as a record for immutability and concise syntax.
 */
public record TopicResponse(
        UUID id,
        String name,
        String description,
        OffsetDateTime createdAt
) {
    public static TopicResponse fromEntity(Topic topic) {
        return new TopicResponse(
                topic.getId(),
                topic.getName(),
                topic.getDescription(),
                topic.getCreatedAt()
        );
    }
}
