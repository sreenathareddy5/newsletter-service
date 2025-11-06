package com.acme.newsletter.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscribers")
@Tag(name = "Subscriber Management", description = "API for entering and managing user email IDs and profiles.")
public class SubscriberController {

    @Operation(summary = "Create a new Subscriber", description = "Registers a new user (email signup) to the system.")
    @ApiResponse(responseCode = "201", description = "Subscriber created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid email format")
    @PostMapping
    public ResponseEntity<Subscriber> createSubscriber(@RequestBody Subscriber subscriber) {
        // ... service call ...
        return new ResponseEntity<>(subscriber, HttpStatus.CREATED);
    }

    @Operation(summary = "Unsubscribe Subscriber", description = "Sets the subscriber status to UNSUBSCRIBED and removes profile.")
    @ApiResponse(responseCode = "204", description = "Subscriber deleted/unsubscribed")
    @DeleteMapping("/{subscriberId}")
    public ResponseEntity<Void> deleteSubscriber(@PathVariable Long subscriberId) {
        // ... service call ...
        return ResponseEntity.noContent().build();
    }
}