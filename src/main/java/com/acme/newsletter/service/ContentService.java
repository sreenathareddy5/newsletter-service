package com.acme.newsletter.service;

import com.acme.newsletter.model.dto.ContentResponse;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContentService {

    ContentResponse createContent(UUID topicId, String title, String body, OffsetDateTime scheduledTime);

    List<ContentResponse> getAllContent();

    Optional<ContentResponse> getContentById(UUID id);

    void deleteContent(UUID id);
}
