package com.acme.newsletter.service;

import com.acme.newsletter.model.Content;
import com.acme.newsletter.model.Topic;
import com.acme.newsletter.model.dto.ContentCreationRequest;
import com.acme.newsletter.repository.ContentRepository;
import com.acme.newsletter.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContentServiceImpl implements ContentService {
    
    private final ContentRepository contentRepository;
    private final TopicRepository topicRepository;

    @Override
    public Content createContent(ContentCreationRequest request) {
        // 1. Validate Topic existence
        Topic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Topic ID not found."));

        // 2. Prevent scheduling content on a date where content for that topic already exists
        // (The unique constraint on topic_id and scheduled_for_date in the DB will also enforce this)
        if (contentRepository.findAll().stream()
                .anyMatch(c -> c.getTopic().getId().equals(topic.getId()) && c.getScheduledForDate().equals(request.getScheduledForDate()))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Content for Topic ID " + topic.getId() + " is already scheduled for " + request.getScheduledForDate());
        }

        // 3. Create and save the new content entity
        Content newContent = new Content(
                topic,
                request.getSubject(),
                request.getBody(),
                request.getScheduledForDate()
        );

        return contentRepository.save(newContent);
    }
    
    // Interface definition for ContentService
    public interface ContentService {
        Content createContent(ContentCreationRequest request);
    }
}