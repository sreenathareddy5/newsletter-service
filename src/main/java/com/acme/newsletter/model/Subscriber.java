package com.acme.newsletter.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "subscriber")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Subscriber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(unique = true, nullable = false)
    private String email;

    @NonNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "subscription_date", nullable = false)
    private LocalDateTime subscriptionDate = LocalDateTime.now();
}