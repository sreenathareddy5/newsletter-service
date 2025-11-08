package com.acme.newsletter.controller;

import com.acme.newsletter.model.dto.ContentResponse;
import com.acme.newsletter.model.dto.CreateContentRequest;
import com.acme.newsletter.service.ContentService;
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
@RequestMapping("/api/content")
@RequiredArgsConstructor
@Tag(name = "Content", description = "Manage newsletter content scheduling and publishing")
public class ContentController {

    private final ContentService contentService;

    @Operation(summary = "Create new content", description = "Schedules new newsletter content for a topic.")
    @ApiResponse(responseCode = "201", description = "Content created successfully")
    @PostMapping
    public ResponseEntity<ContentResponse> create(@RequestBody @Valid CreateContentRequest req) {
        ContentResponse response = contentService.createContent(
                req.getTopicId(),
                req.getTitle(),
                req.getBody(),
                req.getScheduledTime()
        );
        // Use record accessor 'id()' instead of getId()
        return ResponseEntity.created(URI.create("/api/content/" + response.id())).body(response);
    }

    @Operation(summary = "List all content", description = "Fetches all scheduled content items.")
    @ApiResponse(responseCode = "200", description = "Content list retrieved successfully")
    @GetMapping
    public ResponseEntity<List<ContentResponse>> list() {
        List<ContentResponse> responses = contentService.getAllContent();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get content by ID", description = "Fetches details of a specific content item by ID.")
    @ApiResponse(responseCode = "200", description = "Content retrieved successfully")
    @GetMapping("/{id}")
    public ResponseEntity<ContentResponse> get(@PathVariable UUID id) {
        return contentService.getContentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete content by ID", description = "Removes content from schedule or history.")
    @ApiResponse(responseCode = "204", description = "Content deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        contentService.deleteContent(id);
        return ResponseEntity.noContent().build();
    }
}
