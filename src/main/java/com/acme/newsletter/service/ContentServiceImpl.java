package com.acme.newsletter.service.impl;

import com.acme.newsletter.model.Content;
import com.acme.newsletter.model.Topic;
import com.acme.newsletter.model.dto.ContentResponse;
import com.acme.newsletter.repository.ContentRepository;
import com.acme.newsletter.repository.TopicRepository;
import com.acme.newsletter.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContentServiceImpl implements ContentService {

    private final ContentRepository contentRepository;
    private final TopicRepository topicRepository;

    @Override
    public ContentResponse createContent(UUID topicId, String title, String body, OffsetDateTime scheduledTime) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found"));

        boolean exists = contentRepository.existsByTopicAndScheduledTime(topic, scheduledTime);
        if (exists) {
            throw new IllegalStateException("Content already scheduled for this topic and time");
        }

        Content content = Content.builder()
                .topic(topic)
                .title(title)
                .body(body)
                .scheduledTime(scheduledTime)
                .status(Content.Status.PENDING)
                .build();

        Content savedContent = contentRepository.save(content);
        return ContentResponse.fromEntity(savedContent);
    }

    @Override
    public List<ContentResponse> getAllContent() {
        return contentRepository.findAll()
                .stream()
                .map(ContentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteContent(UUID id) {
        if (!contentRepository.existsById(id)) {
            throw new RuntimeException("Content not found");
        }
        contentRepository.deleteById(id);
    }

    @Override
    public Optional<ContentResponse> getContentById(UUID id) {
        return contentRepository.findById(id)
                .map(ContentResponse::fromEntity);
    }
}
