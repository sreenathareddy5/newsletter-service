package com.acme.newsletter.repository;

import com.acme.newsletter.model.Content;
import com.acme.newsletter.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
public interface ContentRepository extends JpaRepository<Content, UUID> {
    boolean existsByTopicAndScheduledTime(Topic topic, OffsetDateTime scheduledTime);
    List<Content> findByStatusAndScheduledTimeBefore(Content.Status status, OffsetDateTime time);

    // Fetch content with topic eagerly
    @Query("SELECT c FROM Content c JOIN FETCH c.topic WHERE c.status = :status AND c.scheduledTime <= :time")
    List<Content> findPendingWithTopic(@Param("status") Content.Status status,
                                       @Param("time") OffsetDateTime time);
}