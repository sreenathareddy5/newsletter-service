package com.acme.newsletter.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "topic")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(unique = true, nullable = false, length = 50)
    private String name; // e.g., "Technology News"

    @NonNull
    @Column(name = "send_time", nullable = false, length = 5)
    // Stores the time (HH:mm) the newsletter for this topic should be sent daily
    private String sendTime;
}