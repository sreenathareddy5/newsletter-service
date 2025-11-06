package com.acme.newsletter.repository;

import com.acme.newsletter.model.Subscriber;
import com.acme.newsletter.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    // Crucial query for the scheduler: retrieves ACTIVE subscribers for a given topic
    @Query("SELECT s FROM Subscriber s JOIN Subscription sub ON s.id = sub.subscriberId " +
           "WHERE sub.topicId = :topicId AND s.isActive = true")
    List<Subscriber> findActiveSubscribersByTopic(@Param("topicId") Long topicId);

    // Used to find all subscriptions for a specific user to easily manage their topics
    List<Subscription> findBySubscriberId(Long subscriberId);
    
    // Used to delete old subscriptions when updating a user's topic list
    @Transactional
    @Modifying
    void deleteBySubscriberId(Long subscriberId);

    boolean existsBySubscriberIdAndTopicId(Long subscriberId, Long topicId);
}