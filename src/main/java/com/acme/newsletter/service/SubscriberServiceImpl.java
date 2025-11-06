package com.acme.newsletter.service;

import com.acme.newsletter.model.Subscriber;
import com.acme.newsletter.model.Subscription;
import com.acme.newsletter.repository.SubscriberRepository;
import com.acme.newsletter.repository.SubscriptionRepository;
import com.acme.newsletter.repository.TopicRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriberServiceImpl implements SubscriberService {

    private final SubscriberRepository subscriberRepository;
    private final TopicRepository topicRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Override
    @Transactional
    public Subscriber subscribe(String email, List<Long> topicIds) {
        // 1. Check if subscriber already exists
        Subscriber subscriber = subscriberRepository.findByEmail(email).orElse(null);

        if (subscriber == null) {
            // New subscriber: create and save
            subscriber = new Subscriber(email);
            subscriber = subscriberRepository.save(subscriber);
            log.info("New subscriber created: {}", email);
        } else {
            // Existing subscriber: just ensure they are active
            if (!subscriber.getIsActive()) {
                subscriber.setIsActive(true);
                subscriber = subscriberRepository.save(subscriber);
                log.info("Existing subscriber reactivated: {}", email);
            }
        }

        final Long subscriberId = subscriber.getId();
        
        // 2. Validate topics and create new subscriptions
        for (Long topicId : topicIds) {
            // Check if the topic ID is valid
            if (!topicRepository.existsById(topicId)) {
                log.warn("Attempted subscription to non-existent topic ID: {}", topicId);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Topic ID " + topicId + " is invalid.");
            }
            
            // Check if subscription already exists before creating
            if (!subscriptionRepository.existsBySubscriberIdAndTopicId(subscriberId, topicId)) {
                Subscription newSubscription = new Subscription();
                newSubscription.setSubscriberId(subscriberId);
                newSubscription.setTopicId(topicId);
                subscriptionRepository.save(newSubscription);
                log.debug("Subscriber {} subscribed to Topic ID {}", email, topicId);
            }
        }

        return subscriber;
    }

    @Override
    @Transactional
    public void unsubscribe(String email) {
        Subscriber subscriber = subscriberRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscriber not found: " + email));

        if (subscriber.getIsActive()) {
            // Only deactivate the subscriber; do not delete subscriptions or the user record
            subscriber.setIsActive(false);
            subscriberRepository.save(subscriber);
            log.info("Subscriber successfully deactivated: {}", email);
        } else {
            log.info("Subscriber was already inactive: {}", email);
        }
    }

    @Override
    @Transactional
    public Subscriber updateTopics(String email, List<Long> newTopicIds) {
        Subscriber subscriber = subscriberRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscriber not found: " + email));

        final Long subscriberId = subscriber.getId();

        // 1. Delete all existing subscriptions for this user
        subscriptionRepository.deleteBySubscriberId(subscriberId);
        log.info("Cleared all old subscriptions for: {}", email);

        // 2. Create new subscriptions based on the provided list
        for (Long topicId : newTopicIds) {
            if (!topicRepository.existsById(topicId)) {
                log.warn("Attempted update with non-existent topic ID: {}", topicId);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Topic ID " + topicId + " is invalid.");
            }

            Subscription newSubscription = new Subscription();
            newSubscription.setSubscriberId(subscriberId);
            newSubscription.setTopicId(topicId);
            subscriptionRepository.save(newSubscription);
            log.debug("Subscriber {} subscribed to new Topic ID {}", email, topicId);
        }

        // Ensure subscriber is active after a topic update
        if (!subscriber.getIsActive()) {
            subscriber.setIsActive(true);
            subscriberRepository.save(subscriber);
        }
        
        return subscriber;
    }
}