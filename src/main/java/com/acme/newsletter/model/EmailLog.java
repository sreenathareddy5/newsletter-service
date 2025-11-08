package com.acme.newsletter.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EmailLog {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    private Subscriber subscriber;

    @ManyToOne
    private Content content;

    private boolean success;
    private String errorMessage;
    private OffsetDateTime sentAt;

    @PrePersist
    public void onCreate() {
        sentAt = OffsetDateTime.now();
    }
}
