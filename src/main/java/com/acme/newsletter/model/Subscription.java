package com.acme.newsletter.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "subscription")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {

    // Composite primary key composed of the two foreign keys
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subscriber_id")
    private Long subscriberId;

    @Column(name = "topic_id")
    private Long topicId;
    
    // Note: The relationships are defined by IDs here for simplicity in repository queries,
    // but in a fully object-oriented model, you might use @ManyToOne mapping here.
}