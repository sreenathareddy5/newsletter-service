package com.acme.newsletter.controller;

import com.acme.newsletter.model.dto.CreateTopicRequest;
import com.acme.newsletter.model.dto.TopicResponse;
import com.acme.newsletter.service.TopicService;
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
@RequestMapping("/api/topics")
@RequiredArgsConstructor
@Tag(name = "Topics", description = "Manage newsletter topics")
public class TopicController {

    private final TopicService topicService;

    @Operation(summary = "Create a new topic", description = "Adds a new topic for newsletter categorization.")
    @ApiResponse(responseCode = "201", description = "Topic created successfully")
    @PostMapping
    public ResponseEntity<TopicResponse> create(@RequestBody @Valid CreateTopicRequest req) {
        TopicResponse response = topicService.createTopic(req.getName(), req.getDescription());
        // Use record accessor 'id()' instead of getId()
        return ResponseEntity.created(URI.create("/api/topics/" + response.id())).body(response);
    }

    @Operation(summary = "List all topics")
    @ApiResponse(responseCode = "200", description = "Topics fetched successfully")
    @GetMapping
    public ResponseEntity<List<TopicResponse>> list() {
        List<TopicResponse> responses = topicService.getAllTopics();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get topic by ID")
    @ApiResponse(responseCode = "200", description = "Topic fetched successfully")
    @GetMapping("/{id}")
    public ResponseEntity<TopicResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(topicService.getTopicById(id));
    }

    @Operation(summary = "Delete a topic by ID")
    @ApiResponse(responseCode = "204", description = "Topic deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        topicService.deleteTopic(id);
        return ResponseEntity.noContent().build();
    }
}
