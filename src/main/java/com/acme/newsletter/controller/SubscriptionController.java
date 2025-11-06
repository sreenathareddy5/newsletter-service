package com.acme.newsletter.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Tag(name = "Subscription Management", description = "API for linking Subscribers to Topics (Subscription service).")
public class SubscriptionController {

    @Operation(summary = "Subscribe User to Topic", description = "Creates a new subscription link between an existing subscriber and a topic.")
    @ApiResponse(responseCode = "201", description = "Subscription created")
    @PostMapping("/subscriptions")
    public ResponseEntity<Subscription> createSubscription(@RequestBody Subscription subscription) {
        // ... service call ...
        return new ResponseEntity<>(subscription, HttpStatus.CREATED);
    }

    // ... (Other endpoints like GET /subscriptions and DELETE /subscriptions/{id}) ...
}