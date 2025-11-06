package com.acme.newsletter.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/content")
@Tag(name = "Content Scheduling", description = "API for creating and scheduling content text to be sent at a specific time.")
public class ContentController {

    @Operation(summary = "Schedule New Content", 
               description = "Schedules a new newsletter message, linking it to a topic and setting the send time.")
    @ApiResponse(responseCode = "201", description = "Content scheduled successfully")
    @PostMapping
    public ResponseEntity<Content> scheduleContent(@RequestBody Content content) {
        // ... service call ...
        return new ResponseEntity<>(content, HttpStatus.CREATED);
    }

    @Operation(summary = "Update Content Schedule", description = "Modifies the content text or the scheduledSendTime.")
    @ApiResponse(responseCode = "200", description = "Content updated")
    @PutMapping("/{contentId}")
    public ResponseEntity<Content> updateContent(@PathVariable Long contentId, @RequestBody Content contentDetails) {
        // ... service call ...
        return ResponseEntity.ok(contentDetails);
    }
}