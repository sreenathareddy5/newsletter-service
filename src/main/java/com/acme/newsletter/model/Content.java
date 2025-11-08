package com.acme.newsletter.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Content {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    @Column(nullable = false)
    private OffsetDateTime scheduledTime;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    private OffsetDateTime sentAt;

    public enum Status {
        PENDING, SENT, FAILED
    }
}
