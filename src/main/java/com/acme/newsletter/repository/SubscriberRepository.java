package com.acme.newsletter.repository;

import com.acme.newsletter.model.Subscriber;
import com.acme.newsletter.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface SubscriberRepository extends JpaRepository<Subscriber, UUID> {
    /**
     * Finds a subscriber by email address, if it exists.
     *
     * @param email subscriber email
     * @return optional subscriber
     */
    Optional<Subscriber> findByEmail(String email);
}