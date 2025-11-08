package com.acme.newsletter.repository;


import com.acme.newsletter.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface TopicRepository extends JpaRepository<Topic, UUID> {

}