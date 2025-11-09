package com.acme.newsletter.service;

import com.acme.newsletter.exception.ResourceNotFoundException;
import com.acme.newsletter.model.Subscriber;
import com.acme.newsletter.model.Subscription;
import com.acme.newsletter.model.Topic;
import com.acme.newsletter.model.dto.SubscriptionResponse;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceImplTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private SubscriberRepository subscriberRepository;

    @Mock
    private TopicRepository topicRepository;

    @InjectMocks
    private com.acme.newsletter.service.impl.SubscriptionServiceImpl subscriptionService;

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
                .email("user@example.com")
                .active(true)
                .build();

        topic = Topic.builder()
                .id(topicId)
                .name("Tech")
                .description("Technology updates")
                .build();

        subscription = Subscription.builder()
                .id(UUID.randomUUID())
                .subscriber(subscriber)
                .topic(topic)
                .active(true)
                .build();
    }

    // ---------- subscribe ----------

    @Test
    @DisplayName("subscribe → should create new subscription if none exists")
    void shouldCreateNewSubscription() {
        when(subscriberRepository.findById(subscriberId)).thenReturn(Optional.of(subscriber));
        when(topicRepository.findById(topicId)).thenReturn(Optional.of(topic));
        when(subscriptionRepository.findBySubscriberAndTopic(subscriber, topic)).thenReturn(Optional.empty());
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(subscription);

        SubscriptionResponse response = subscriptionService.subscribe(subscriberId, topicId);

        assertThat(response.topicName()).isEqualTo("Tech");
        verify(subscriptionRepository).save(any(Subscription.class));
    }

    @Test
    @DisplayName("subscribe → should reuse existing subscription if present")
    void shouldReuseExistingSubscription() {
        when(subscriberRepository.findById(subscriberId)).thenReturn(Optional.of(subscriber));
        when(topicRepository.findById(topicId)).thenReturn(Optional.of(topic));
        when(subscriptionRepository.findBySubscriberAndTopic(subscriber, topic)).thenReturn(Optional.of(subscription));

        SubscriptionResponse response = subscriptionService.subscribe(subscriberId, topicId);

        assertThat(response.topicId()).isEqualTo(topicId);
        verify(subscriptionRepository, never()).save(any());
    }

    @Test
    @DisplayName("subscribe → should throw if subscriber not found")
    void shouldThrowIfSubscriberNotFound() {
        when(subscriberRepository.findById(subscriberId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subscriptionService.subscribe(subscriberId, topicId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Subscriber not found");
    }

    @Test
    @DisplayName("subscribe → should throw if topic not found")
    void shouldThrowIfTopicNotFound() {
        when(subscriberRepository.findById(subscriberId)).thenReturn(Optional.of(subscriber));
        when(topicRepository.findById(topicId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subscriptionService.subscribe(subscriberId, topicId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Topic not found");
    }

    // ---------- unsubscribe ----------

    @Test
    @DisplayName("unsubscribe → should deactivate subscription")
    void shouldUnsubscribeSuccessfully() {
        when(subscriberRepository.findById(subscriberId)).thenReturn(Optional.of(subscriber));
        when(topicRepository.findById(topicId)).thenReturn(Optional.of(topic));
        when(subscriptionRepository.findBySubscriberAndTopic(subscriber, topic)).thenReturn(Optional.of(subscription));

        subscriptionService.unsubscribe(subscriberId, topicId);

        assertThat(subscription.isActive()).isFalse();
        verify(subscriptionRepository).save(subscription);
    }

    @Test
    @DisplayName("unsubscribe → should throw if subscriber missing")
    void shouldThrowIfSubscriberMissing() {
        when(subscriberRepository.findById(subscriberId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subscriptionService.unsubscribe(subscriberId, topicId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Subscriber not found");
    }

    @Test
    @DisplayName("unsubscribe → should throw if topic missing")
    void shouldThrowIfTopicMissing() {
        when(subscriberRepository.findById(subscriberId)).thenReturn(Optional.of(subscriber));
        when(topicRepository.findById(topicId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subscriptionService.unsubscribe(subscriberId, topicId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Topic not found");
    }

    @Test
    @DisplayName("unsubscribe → should throw if subscription missing")
    void shouldThrowIfSubscriptionMissing() {
        when(subscriberRepository.findById(subscriberId)).thenReturn(Optional.of(subscriber));
        when(topicRepository.findById(topicId)).thenReturn(Optional.of(topic));
        when(subscriptionRepository.findBySubscriberAndTopic(subscriber, topic)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subscriptionService.unsubscribe(subscriberId, topicId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Subscription not found");
    }

    // ---------- getSubscriptionsBySubscriber ----------

    @Test
    @DisplayName("getSubscriptionsBySubscriber → should return subscriptions for subscriber")
    void shouldReturnSubscriptionsBySubscriber() {
        when(subscriberRepository.findById(subscriberId)).thenReturn(Optional.of(subscriber));
        when(subscriptionRepository.findBySubscriber(subscriber)).thenReturn(List.of(subscription));

        List<SubscriptionResponse> result = subscriptionService.getSubscriptionsBySubscriber(subscriberId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).topicName()).isEqualTo("Tech");
    }

    @Test
    @DisplayName("getSubscriptionsBySubscriber → should throw if subscriber not found")
    void shouldThrowIfSubscriberNotFoundOnFetch() {
        when(subscriberRepository.findById(subscriberId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subscriptionService.getSubscriptionsBySubscriber(subscriberId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Subscriber not found");
    }

    // ---------- getAllSubscriptions ----------

    @Test
    @DisplayName("getAllSubscriptions → should return all subscriptions")
    void shouldReturnAllSubscriptions() {
        when(subscriptionRepository.findAll()).thenReturn(List.of(subscription));

        List<SubscriptionResponse> result = subscriptionService.getAllSubscriptions();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).topicName()).isEqualTo("Tech");
    }

    // ---------- getSubscribersByTopic ----------

    @Test
    @DisplayName("getSubscribersByTopic → should return subscribers for topic")
    void shouldReturnSubscribersByTopic() {
        when(topicRepository.findById(topicId)).thenReturn(Optional.of(topic));
        when(subscriptionRepository.findByTopic(topic)).thenReturn(List.of(subscription));

        var result = subscriptionService.getSubscribersByTopic(topicId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).email()).isEqualTo("user@example.com");
    }

    @Test
    @DisplayName("getSubscribersByTopic → should throw if topic not found")
    void shouldThrowIfTopicNotFoundOnFetch() {
        when(topicRepository.findById(topicId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subscriptionService.getSubscribersByTopic(topicId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Topic not found");
    }
}
