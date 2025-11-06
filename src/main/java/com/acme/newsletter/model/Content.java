package com.acme.newsletter.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "content")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Use eager fetch to ensure Topic details are available in the scheduler
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "topic_id", nullable = false)
    @NonNull
    private Topic topic; // Links content to a specific topic

    @NonNull
    @Column(nullable = false)
    private String subject;

    @NonNull
    @Column(columnDefinition = "TEXT", nullable = false)
    private String body;

    @NonNull
    @Column(name = "scheduled_for_date", nullable = false)
    private LocalDate scheduledForDate; // The date this newsletter is meant to be sent

    @Column(name = "is_sent", nullable = false)
    private Boolean isSent = false;
}