package com.acme.newsletter.service;

import com.acme.newsletter.model.dto.SubscriptionResponse;
import com.acme.newsletter.model.dto.SubscriberResponse;

import java.util.List;
import java.util.UUID;

public interface SubscriptionService {

    /**
     * Subscribes a user to a topic and returns subscription info as DTO.
     */
    SubscriptionResponse subscribe(UUID subscriberId, UUID topicId);

    /**
     * Unsubscribes a user from a topic.
     */
    void unsubscribe(UUID subscriberId, UUID topicId);

    /**
     * Returns all subscriptions for a given subscriber as DTOs.
     */
    List<SubscriptionResponse> getSubscriptionsBySubscriber(UUID subscriberId);

    /**
     * Returns all active subscriptions in the system as DTOs.
     */
    List<SubscriptionResponse> getAllSubscriptions();

    /**
     * Returns all subscribers for a topic as DTOs.
     */
    List<SubscriberResponse> getSubscribersByTopic(UUID topicId);
}
