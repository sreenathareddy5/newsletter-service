package com.acme.newsletter.service;

import com.acme.newsletter.model.Subscriber;
import com.acme.newsletter.model.Subscription;
import com.acme.newsletter.model.Topic;
import com.acme.newsletter.model.dto.SubscriberResponse;
import com.acme.newsletter.repository.SubscriberRepository;
import com.acme.newsletter.repository.SubscriptionRepository;
import com.acme.newsletter.repository.TopicRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriberServiceImplTest {

    @Mock
    private SubscriberRepository subscriberRepository;

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private com.acme.newsletter.service.impl.SubscriberServiceImpl subscriberService;

    private Subscriber subscriber;
    private Topic topic;
    private Subscription subscription;
    private UUID subscriberId;
    private UUID topicId;

    @BeforeEach
    void setup() {
        subscriberId = UUID.randomUUID();
        topicId = UUID.randomUUID();

        subscriber = Subscriber.builder()
                .id(subscriberId)
                .email("test@example.com")
                .active(true)
                .build();

        topic = Topic.builder()
                .id(topicId)
                .name("Technology")
                .description("Tech News")
                .build();

        subscription = Subscription.builder()
                .id(UUID.randomUUID())
                .subscriber(subscriber)
                .topic(topic)
                .active(true)
                .build();
    }

    // ---------- createSubscriber ----------

    @Test
    @DisplayName("createSubscriber → should create new subscriber and subscribe to topic")
    void shouldCreateSubscriberAndSubscribe() {
        when(subscriberRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(subscriberRepository.save(any(Subscriber.class))).thenReturn(subscriber);
        when(topicRepository.findById(topicId)).thenReturn(Optional.of(topic));
        when(subscriptionRepository.findBySubscriberAndTopic(subscriber, topic)).thenReturn(Optional.empty());
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(subscription);

        SubscriberResponse response = subscriberService.createSubscriber("test@example.com", topicId);

        assertThat(response.email()).isEqualTo("test@example.com");
        verify(subscriberRepository).save(any(Subscriber.class));
        verify(subscriptionRepository).save(any(Subscription.class));
    }

    @Test
    @DisplayName("createSubscriber → should reuse existing subscriber if already present")
    void shouldReuseExistingSubscriber() {
        when(subscriberRepository.findByEmail("test@example.com")).thenReturn(Optional.of(subscriber));
        when(topicRepository.findById(topicId)).thenReturn(Optional.of(topic));
        when(subscriptionRepository.findBySubscriberAndTopic(subscriber, topic)).thenReturn(Optional.of(subscription));

        SubscriberResponse response = subscriberService.createSubscriber("test@example.com", topicId);

        assertThat(response.email()).isEqualTo("test@example.com");
        verify(subscriberRepository, never()).save(any());
        verify(subscriptionRepository, never()).save(any());
    }

    @Test
    @DisplayName("createSubscriber → should throw if topic not found")
    void shouldThrowIfTopicNotFound() {
        when(subscriberRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(subscriberRepository.save(any(Subscriber.class))).thenReturn(subscriber);
        when(topicRepository.findById(topicId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subscriberService.createSubscriber("test@example.com", topicId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Topic not found");
    }

    // ---------- getAllSubscribers ----------

    @Test
    @DisplayName("getAllSubscribers → should return list of all subscribers")
    void shouldReturnAllSubscribers() {
        when(subscriberRepository.findAll()).thenReturn(List.of(subscriber));

        List<SubscriberResponse> result = subscriberService.getAllSubscribers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).email()).isEqualTo("test@example.com");
    }

    // ---------- getSubscribersByTopic ----------

    @Test
    @DisplayName("getSubscribersByTopic → should return active subscribers for topic")
    void shouldReturnSubscribersByTopic() {
        when(topicRepository.findById(topicId)).thenReturn(Optional.of(topic));
        when(subscriptionRepository.findByTopicAndActiveTrue(topic)).thenReturn(List.of(subscription));

        List<SubscriberResponse> result = subscriberService.getSubscribersByTopic(topicId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).email()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("getSubscribersByTopic → should throw when topic not found")
    void shouldThrowWhenTopicMissing() {
        when(topicRepository.findById(topicId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subscriberService.getSubscribersByTopic(topicId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Topic not found");
    }

    // ---------- deleteSubscriber ----------

    @Test
    @DisplayName("deleteSubscriber → should delete subscriber and their subscriptions")
    void shouldDeleteSubscriberAndSubscriptions() {
        when(subscriberRepository.findById(subscriberId)).thenReturn(Optional.of(subscriber));
        when(subscriptionRepository.findBySubscriber(subscriber)).thenReturn(List.of(subscription));

        subscriberService.deleteSubscriber(subscriberId);

        verify(subscriptionRepository).delete(subscription);
        verify(subscriberRepository).delete(subscriber);
    }

    @Test
    @DisplayName("deleteSubscriber → should throw when subscriber not found")
    void shouldThrowWhenSubscriberMissing() {
        when(subscriberRepository.findById(subscriberId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subscriberService.deleteSubscriber(subscriberId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Subscriber not found");
    }
}
