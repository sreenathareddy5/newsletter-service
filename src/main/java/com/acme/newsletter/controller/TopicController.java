package com.acme.newsletter.controller;

import com.acme.newsletter.model.dto.Topic;
import com.acme.newsletter.model.dto.TopicRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/topics")
@Tag(name = "Topic Management", description = "Admin API for managing content categories. Content is segregated on Topic basis.")
public class TopicController {


    /**
     * POST /api/topics : Create a new topic
     */
    @Operation(summary = "Create a new Topic", 
               description = "Creates a new topic based on the provided name and description.")
    @ApiResponse(responseCode = "201", description = "Topic created successfully", 
                 content = @Content(schema = @Schema(implementation = Topic.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input or Topic name already exists")
    @PostMapping
    public ResponseEntity<Topic> createTopic(@RequestBody TopicRequest request) {
        // --- Service Logic Placeholder ---
        Topic newTopic = new Topic(99L, request.getName(), request.getDescription());
        // Topic created = topicService.save(newTopic);
        
        return new ResponseEntity<>(newTopic, HttpStatus.CREATED); 
    }

    /**
     * GET /api/topics : Retrieve a list of all available topics
     */
    @Operation(summary = "Get All Topics", 
               description = "Retrieves a list of all available content topics.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of topics",
                 content = @Content(array = @ArraySchema(schema = @Schema(implementation = Topic.class))))
    @GetMapping
    public ResponseEntity<List<Topic>> getAllTopics() {
        // --- Service Logic Placeholder ---
        List<Topic> topics = List.of(
            new Topic(1L, "Tech News", "The latest in gadgets and AI."),
            new Topic(2L, "World Markets", "Global stock and bond market summaries.")
        );
        // return ResponseEntity.ok(topicService.findAll());
        
        return ResponseEntity.ok(topics);
    }

    /**
     * GET /api/topics/{topicId} : Retrieve a specific topic by its ID
     */
    @Operation(summary = "Get Topic by ID", 
               description = "Retrieves a single topic using its unique ID.")
    @ApiResponse(responseCode = "200", description = "Topic found and returned", 
                 content = @Content(schema = @Schema(implementation = Topic.class)))
    @ApiResponse(responseCode = "404", description = "Topic not found")
    @GetMapping("/{topicId}")
    public ResponseEntity<Topic> getTopicById(@PathVariable Long topicId) {
        // --- Service Logic Placeholder ---
        if (topicId.equals(1L)) {
            return ResponseEntity.ok(new Topic(topicId, "Tech News", "Latest in tech."));
        }
        // return topicService.findById(topicId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        
        return ResponseEntity.notFound().build();
    }

    /**
     * PUT /api/topics/{topicId} : Update the details of an existing topic
     */
    @Operation(summary = "Update an existing Topic", 
               description = "Updates the name or description of an existing topic.")
    @ApiResponse(responseCode = "200", description = "Topic updated successfully",
                 content = @Content(schema = @Schema(implementation = Topic.class)))
    @ApiResponse(responseCode = "404", description = "Topic not found")
    @PutMapping("/{topicId}")
    public ResponseEntity<Topic> updateTopic(@PathVariable Long topicId, @RequestBody TopicRequest request) {
        // --- Service Logic Placeholder ---
        // Topic updated = topicService.update(topicId, request);
        // return ResponseEntity.ok(updated);
        if (topicId.equals(1L)) {
            return ResponseEntity.ok(new Topic(topicId, request.getName(), request.getDescription()));
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * DELETE /api/topics/{topicId} : Delete a topic
     */
    @Operation(summary = "Delete a Topic", 
               description = "Permanently deletes a topic by ID. Requires administrative privileges.")
    @ApiResponse(responseCode = "204", description = "Topic successfully deleted")
    @ApiResponse(responseCode = "404", description = "Topic not found")
    @DeleteMapping("/{topicId}")
    public ResponseEntity<Void> deleteTopic(@PathVariable Long topicId) {
        // --- Service Logic Placeholder ---
        // topicService.delete(topicId);
        // return ResponseEntity.noContent().build();
        
        return ResponseEntity.noContent().build();
    }
}