package com.acme.newsletter.repository;

import com.acme.newsletter.model.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {

    // The key query for the scheduler: finds content that matches today's date and the current minute's topic time.
    @Query("SELECT c FROM Content c JOIN c.topic t " +
            "WHERE c.scheduledForDate = :today " +
            "AND t.sendTime = :currentTime " +
            "AND c.isSent = false")
    List<Content> findContentReadyToSend(
            @Param("today") LocalDate today,
            @Param("currentTime") String currentTime);
}