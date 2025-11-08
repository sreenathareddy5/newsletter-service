package com.acme.newsletter.repository;

import com.acme.newsletter.model.Subscription;
import com.acme.newsletter.model.Topic;
import com.acme.newsletter.model.Subscriber;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    /**
     * Finds all active subscriptions for a given topic.
     */
    // Use JOIN FETCH to eagerly fetch the subscriber entity
    @Query("SELECT s FROM Subscription s JOIN FETCH s.subscriber WHERE s.topic = :topic AND s.active = true")
    List<Subscription> findByTopicAndActiveTrue(@Param("topic") Topic topic);

    /**
     * Finds all subscriptions for a given subscriber.
     */
    List<Subscription> findBySubscriber(Subscriber subscriber);

    /**
     * Find a subscription by subscriber and topic combination.
     */
    Optional<Subscription> findBySubscriberAndTopic(Subscriber subscriber, Topic topic);

    List<Subscription> findByTopic(Topic topic);

    // Fetch subscribers for a topic in pages
    Page<Subscription> findByTopicAndActiveTrue(Topic topic, Pageable pageable);
}
