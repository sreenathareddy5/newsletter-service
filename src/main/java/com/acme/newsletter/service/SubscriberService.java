package com.acme.newsletter.service;

import com.acme.newsletter.model.Subscriber;

import java.util.List;

public interface SubscriberService {
    Subscriber subscribe(String email, List<Long> topicIds);
    void unsubscribe(String email);
    Subscriber updateTopics(String email, List<Long> newTopicIds);
}