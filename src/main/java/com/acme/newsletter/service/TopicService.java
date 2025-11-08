package com.acme.newsletter.service;

import com.acme.newsletter.model.dto.TopicResponse;
import java.util.List;
import java.util.UUID;

public interface TopicService {

    TopicResponse createTopic(String name, String description);

    List<TopicResponse> getAllTopics();

    TopicResponse getTopicById(UUID id);

    void deleteTopic(UUID id);
}
