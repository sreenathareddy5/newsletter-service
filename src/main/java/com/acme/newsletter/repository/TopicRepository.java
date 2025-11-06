package com.acme.newsletter.repository;

import com.acme.newsletter.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    // Basic CRUD inherited from JpaRepository is sufficient for topics
}