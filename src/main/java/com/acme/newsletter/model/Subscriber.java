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
public class Subscriber {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    private boolean active = true;
    @Column(name = "subscribed_at", nullable = false)
    private OffsetDateTime subscribedAt;
    @PrePersist
    public void prePersist() {
        subscribedAt = OffsetDateTime.now();
    }
}
