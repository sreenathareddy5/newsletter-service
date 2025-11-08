package com.acme.newsletter.service;

import com.acme.newsletter.model.dto.SubscriberResponse;

import java.util.List;
import java.util.UUID;

public interface SubscriberService {

    /**
     * Registers a new subscriber (and optionally subscribes to a topic).
     */
    SubscriberResponse createSubscriber(String email, UUID topicId);

    /**
     * Returns all subscribers as DTOs.
     */
    List<SubscriberResponse> getAllSubscribers();

    /**
     * Returns all subscribers for a given topic as DTOs.
     */
    List<SubscriberResponse> getSubscribersByTopic(UUID topicId);

    /**
     * Deletes a subscriber.
     */
    void deleteSubscriber(UUID id);
}
