package com.acme.newsletter.controller;

import com.acme.newsletter.model.dto.CreateSubscriberRequest;
import com.acme.newsletter.model.dto.SubscriberResponse;
import com.acme.newsletter.service.SubscriberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/subscribers")
@RequiredArgsConstructor
@Tag(name = "Subscribers", description = "Manage subscribers and topic subscriptions")
public class SubscriberController {

    private final SubscriberService subscriberService;

    @Operation(summary = "Register a new subscriber")
    @ApiResponse(responseCode = "201", description = "Subscriber created successfully")
    @PostMapping
    public ResponseEntity<SubscriberResponse> create(@RequestBody @Valid CreateSubscriberRequest req) {
        SubscriberResponse response = subscriberService.createSubscriber(req.getEmail(), req.getTopicId());
        return ResponseEntity.created(URI.create("/api/subscribers/" + response.id())).body(response);
    }

    @Operation(summary = "List all subscribers")
    @ApiResponse(responseCode = "200", description = "Subscribers retrieved successfully")
    @GetMapping
    public ResponseEntity<List<SubscriberResponse>> list() {
        List<SubscriberResponse> responses = subscriberService.getAllSubscribers();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "List subscribers by topic")
    @ApiResponse(responseCode = "200", description = "Subscribers retrieved successfully")
    @GetMapping("/topic/{topicId}")
    public ResponseEntity<List<SubscriberResponse>> byTopic(@PathVariable UUID topicId) {
        List<SubscriberResponse> responses = subscriberService.getSubscribersByTopic(topicId);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Unsubscribe")
    @ApiResponse(responseCode = "204", description = "Subscriber removed successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        subscriberService.deleteSubscriber(id);
        return ResponseEntity.noContent().build();
    }
}
