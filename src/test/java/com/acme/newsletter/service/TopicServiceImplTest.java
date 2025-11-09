package com.acme.newsletter.service;

import com.acme.newsletter.model.Topic;
import com.acme.newsletter.model.dto.TopicResponse;
import com.acme.newsletter.repository.TopicRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TopicServiceImplTest {

    @Mock
    private TopicRepository topicRepository;

    @InjectMocks
    private TopicServiceImpl topicService;

    private Topic topic;
    private UUID topicId;

    @BeforeEach
    void setup() {
        topicId = UUID.randomUUID();
        topic = Topic.builder()
                .id(topicId)
                .name("Tech")
                .description("Technology news and updates")
                .build();
    }

    // ---------- createTopic ----------

    @Test
    @DisplayName("createTopic → should save and return topic response")
    void shouldCreateTopic() {
        when(topicRepository.save(any(Topic.class))).thenReturn(topic);

        TopicResponse response = topicService.createTopic("Tech", "Technology news and updates");

        assertThat(response.name()).isEqualTo("Tech");
        verify(topicRepository).save(any(Topic.class));
    }

    // ---------- getAllTopics ----------

    @Test
    @DisplayName("getAllTopics → should return list of topics")
    void shouldReturnAllTopics() {
        when(topicRepository.findAll()).thenReturn(List.of(topic));

        List<TopicResponse> responses = topicService.getAllTopics();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).name()).isEqualTo("Tech");
    }

    @Test
    @DisplayName("getAllTopics → should return empty list if no topics exist")
    void shouldReturnEmptyListIfNoTopics() {
        when(topicRepository.findAll()).thenReturn(Collections.emptyList());

        List<TopicResponse> responses = topicService.getAllTopics();

        assertThat(responses).isEmpty();
    }

    // ---------- getTopicById ----------

    @Test
    @DisplayName("getTopicById → should return topic if exists")
    void shouldReturnTopicById() {
        when(topicRepository.findById(topicId)).thenReturn(Optional.of(topic));

        TopicResponse response = topicService.getTopicById(topicId);

        assertThat(response.name()).isEqualTo("Tech");
        verify(topicRepository).findById(topicId);
    }

    @Test
    @DisplayName("getTopicById → should throw exception if topic not found")
    void shouldThrowWhenTopicNotFound() {
        when(topicRepository.findById(topicId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> topicService.getTopicById(topicId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Topic not found");
    }

    // ---------- deleteTopic ----------

    @Test
    @DisplayName("deleteTopic → should delete if exists")
    void shouldDeleteTopicIfExists() {
        when(topicRepository.existsById(topicId)).thenReturn(true);

        topicService.deleteTopic(topicId);

        verify(topicRepository).deleteById(topicId);
    }

    @Test
    @DisplayName("deleteTopic → should throw if topic does not exist")
    void shouldThrowIfTopicDoesNotExist() {
        when(topicRepository.existsById(topicId)).thenReturn(false);

        assertThatThrownBy(() -> topicService.deleteTopic(topicId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Topic not found");
    }
}
