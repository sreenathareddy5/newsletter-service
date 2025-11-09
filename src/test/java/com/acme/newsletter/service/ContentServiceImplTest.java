package com.acme.newsletter.service;

import com.acme.newsletter.model.Content;
import com.acme.newsletter.model.Topic;
import com.acme.newsletter.model.dto.ContentResponse;
import com.acme.newsletter.repository.ContentRepository;
import com.acme.newsletter.repository.TopicRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContentServiceImplTest {

    @Mock
    private ContentRepository contentRepository;

    @Mock
    private TopicRepository topicRepository;

    @InjectMocks
    private com.acme.newsletter.service.impl.ContentServiceImpl contentService;

    private Topic topic;
    private Content content;
    private UUID topicId;
    private OffsetDateTime scheduledTime;

    @BeforeEach
    void setup() {
        topicId = UUID.randomUUID();
        scheduledTime = OffsetDateTime.now().plusDays(1);
        topic = Topic.builder().id(topicId).name("Technology").description("Tech news").build();
        content = Content.builder()
                .id(UUID.randomUUID())
                .topic(topic)
                .title("Weekly Update")
                .body("New gadgets released")
                .scheduledTime(scheduledTime)
                .status(Content.Status.PENDING)
                .build();
    }

    @Test
    @DisplayName("createContent → should create and return content successfully")
    void shouldCreateContentSuccessfully() {
        when(topicRepository.findById(topicId)).thenReturn(Optional.of(topic));
        when(contentRepository.existsByTopicAndScheduledTime(topic, scheduledTime)).thenReturn(false);
        when(contentRepository.save(any(Content.class))).thenReturn(content);

        ContentResponse response = contentService.createContent(topicId, "Weekly Update", "New gadgets released", scheduledTime);

        assertThat(response.title()).isEqualTo("Weekly Update");
        assertThat(response.topicName()).isEqualTo("Technology");
        verify(contentRepository).save(any(Content.class));
    }

    @Test
    @DisplayName("createContent → should throw exception if topic not found")
    void shouldThrowWhenTopicNotFound() {
        when(topicRepository.findById(topicId)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                contentService.createContent(topicId, "Weekly Update", "Body", scheduledTime))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Topic not found");

        verify(contentRepository, never()).save(any());
    }

    @Test
    @DisplayName("createContent → should throw exception if content already scheduled")
    void shouldThrowWhenDuplicateContentExists() {
        when(topicRepository.findById(topicId)).thenReturn(Optional.of(topic));
        when(contentRepository.existsByTopicAndScheduledTime(topic, scheduledTime)).thenReturn(true);

        assertThatThrownBy(() ->
                contentService.createContent(topicId, "Weekly Update", "Body", scheduledTime))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already scheduled");

        verify(contentRepository, never()).save(any());
    }

    @Test
    @DisplayName("getAllContent → should return all content items")
    void shouldReturnAllContent() {
        when(contentRepository.findAll()).thenReturn(List.of(content));

        List<ContentResponse> result = contentService.getAllContent();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).title()).isEqualTo("Weekly Update");
    }

    @Test
    @DisplayName("deleteContent → should delete when exists")
    void shouldDeleteWhenContentExists() {
        UUID contentId = content.getId();
        when(contentRepository.existsById(contentId)).thenReturn(true);

        contentService.deleteContent(contentId);

        verify(contentRepository).deleteById(contentId);
    }

    @Test
    @DisplayName("deleteContent → should throw when content not found")
    void shouldThrowWhenContentNotFoundOnDelete() {
        UUID contentId = UUID.randomUUID();
        when(contentRepository.existsById(contentId)).thenReturn(false);

        assertThatThrownBy(() -> contentService.deleteContent(contentId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Content not found");
    }

    @Test
    @DisplayName("getContentById → should return content when found")
    void shouldReturnContentById() {
        UUID contentId = content.getId();
        when(contentRepository.findById(contentId)).thenReturn(Optional.of(content));

        Optional<ContentResponse> result = contentService.getContentById(contentId);

        assertThat(result).isPresent();
        assertThat(result.get().title()).isEqualTo("Weekly Update");
    }

    @Test
    @DisplayName("getContentById → should return empty when not found")
    void shouldReturnEmptyWhenContentNotFound() {
        UUID contentId = UUID.randomUUID();
        when(contentRepository.findById(contentId)).thenReturn(Optional.empty());

        Optional<ContentResponse> result = contentService.getContentById(contentId);

        assertThat(result).isEmpty();
    }
}
