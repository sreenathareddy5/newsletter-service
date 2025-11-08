package com.acme.newsletter.controller;

import com.acme.newsletter.model.dto.SubscriptionResponse;
import com.acme.newsletter.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
@Tag(name = "Subscriptions", description = "Manage subscriber-topic mappings")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Operation(summary = "List all subscriptions")
    @ApiResponse(responseCode = "200", description = "Subscriptions retrieved successfully")
    @GetMapping
    public ResponseEntity<List<SubscriptionResponse>> list() {
        List<SubscriptionResponse> responses = subscriptionService.getAllSubscriptions();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Subscribe a user to a topic")
    @ApiResponse(responseCode = "201", description = "Subscription created successfully")
    @PostMapping("/{subscriberId}/{topicId}")
    public ResponseEntity<SubscriptionResponse> subscribe(@PathVariable UUID subscriberId, @PathVariable UUID topicId) {
        SubscriptionResponse response = subscriptionService.subscribe(subscriberId, topicId);
        return ResponseEntity.status(201).body(response);
    }

    @Operation(summary = "Unsubscribe user from topic")
    @ApiResponse(responseCode = "204", description = "Subscription removed successfully")
    @DeleteMapping("/{subscriberId}/{topicId}")
    public ResponseEntity<Void> unsubscribe(@PathVariable UUID subscriberId, @PathVariable UUID topicId) {
        subscriptionService.unsubscribe(subscriberId, topicId);
        return ResponseEntity.noContent().build();
    }
}
