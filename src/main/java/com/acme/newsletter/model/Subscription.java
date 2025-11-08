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
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"subscriber_id", "topic_id"})
})
public class Subscription {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subscriber_id")
    private Subscriber subscriber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    private boolean active = true;
    private OffsetDateTime subscribedAt;

    @PrePersist
    public void prePersist() {
        subscribedAt = OffsetDateTime.now();
    }
}
