package com.acme.newsletter.service.impl;

import com.acme.newsletter.model.Subscriber;
import com.acme.newsletter.model.Subscription;
import com.acme.newsletter.model.Topic;
import com.acme.newsletter.model.dto.SubscriberResponse;
import com.acme.newsletter.repository.SubscriberRepository;
import com.acme.newsletter.repository.SubscriptionRepository;
import com.acme.newsletter.repository.TopicRepository;
import com.acme.newsletter.service.SubscriberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriberServiceImpl implements SubscriberService {

    private final SubscriberRepository subscriberRepository;
    private final TopicRepository topicRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Override
    public SubscriberResponse createSubscriber(String email, UUID topicId) {
        Subscriber subscriber = subscriberRepository.findByEmail(email)
                .orElseGet(() -> subscriberRepository.save(
                        Subscriber.builder()
                                .email(email)
                                .active(true)
                                .build()
                ));

        if (topicId != null) {
            Topic topic = topicRepository.findById(topicId)
                    .orElseThrow(() -> new RuntimeException("Topic not found"));

            // Avoid duplicate subscriptions
            subscriptionRepository.findBySubscriberAndTopic(subscriber, topic)
                    .orElseGet(() -> subscriptionRepository.save(
                            Subscription.builder()
                                    .subscriber(subscriber)
                                    .topic(topic)
                                    .active(true)
                                    .build()
                    ));
        }

        return SubscriberResponse.fromEntity(subscriber);
    }

    @Override
    public List<SubscriberResponse> getAllSubscribers() {
        return subscriberRepository.findAll()
                .stream()
                .map(SubscriberResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<SubscriberResponse> getSubscribersByTopic(UUID topicId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found"));

        return subscriptionRepository.findByTopicAndActiveTrue(topic)
                .stream()
                .map(sub -> SubscriberResponse.fromEntity(sub.getSubscriber()))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteSubscriber(UUID id) {
        Subscriber subscriber = subscriberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscriber not found"));

        // Delete all subscriptions for the subscriber
        subscriptionRepository.findBySubscriber(subscriber)
                .forEach(subscriptionRepository::delete);

        subscriberRepository.delete(subscriber);
    }
}
