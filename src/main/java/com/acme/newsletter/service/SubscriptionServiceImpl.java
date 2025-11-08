package com.acme.newsletter.service.impl;

import com.acme.newsletter.exception.ResourceNotFoundException;
import com.acme.newsletter.model.Subscriber;
import com.acme.newsletter.model.Subscription;
import com.acme.newsletter.model.Topic;
import com.acme.newsletter.model.dto.SubscriptionResponse;
import com.acme.newsletter.model.dto.SubscriberResponse;
import com.acme.newsletter.repository.SubscriberRepository;
import com.acme.newsletter.repository.SubscriptionRepository;
import com.acme.newsletter.repository.TopicRepository;
import com.acme.newsletter.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriberRepository subscriberRepository;
    private final TopicRepository topicRepository;

    @Override
    public SubscriptionResponse subscribe(UUID subscriberId, UUID topicId) {
        Subscriber subscriber = subscriberRepository.findById(subscriberId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscriber not found"));
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found"));

        Subscription subscription = subscriptionRepository.findBySubscriberAndTopic(subscriber, topic)
                .orElseGet(() -> subscriptionRepository.save(
                        Subscription.builder()
                                .subscriber(subscriber)
                                .topic(topic)
                                .active(true)
                                .build()
                ));

        return SubscriptionResponse.fromEntity(subscription);
    }

    @Override
    public void unsubscribe(UUID subscriberId, UUID topicId) {
        Subscriber subscriber = subscriberRepository.findById(subscriberId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscriber not found"));
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found"));

        Subscription subscription = subscriptionRepository.findBySubscriberAndTopic(subscriber, topic)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

        subscription.setActive(false);
        subscriptionRepository.save(subscription);
    }

    @Override
    public List<SubscriptionResponse> getSubscriptionsBySubscriber(UUID subscriberId) {
        Subscriber subscriber = subscriberRepository.findById(subscriberId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscriber not found"));

        return subscriptionRepository.findBySubscriber(subscriber)
                .stream()
                .map(SubscriptionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<SubscriptionResponse> getAllSubscriptions() {
        return subscriptionRepository.findAll()
                .stream()
                .map(SubscriptionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<SubscriberResponse> getSubscribersByTopic(UUID topicId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found"));

        return subscriptionRepository.findByTopic(topic)
                .stream()
                .map(sub -> SubscriberResponse.fromEntity(sub.getSubscriber()))
                .collect(Collectors.toList());
    }
}
