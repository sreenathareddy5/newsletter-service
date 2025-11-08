package com.acme.newsletter.service;

import com.acme.newsletter.model.Topic;
import com.acme.newsletter.model.dto.TopicResponse;
import com.acme.newsletter.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;

    @Override
    public TopicResponse createTopic(String name, String description) {
        Topic topic = Topic.builder()
                .name(name)
                .description(description)
                .build();
        Topic savedTopic = topicRepository.save(topic);
        return TopicResponse.fromEntity(savedTopic);
    }

    @Override
    public List<TopicResponse> getAllTopics() {
        return topicRepository.findAll()
                .stream()
                .map(TopicResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public TopicResponse getTopicById(UUID id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Topic not found"));
        return TopicResponse.fromEntity(topic);
    }

    @Override
    public void deleteTopic(UUID id) {
        if (!topicRepository.existsById(id)) {
            throw new IllegalArgumentException("Topic not found");
        }
        topicRepository.deleteById(id);
    }
}
