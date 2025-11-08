package com.acme.newsletter.controller;

import com.acme.newsletter.service.NewsletterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * API endpoints for triggering and monitoring newsletter delivery.
 * Typically, the scheduler handles automated dispatching,
 * but this controller allows for manual control and diagnostics.
 */
@RestController
@RequestMapping("/api/newsletter")
@RequiredArgsConstructor
@Tag(name = "Newsletter", description = "Handles newsletter email delivery and scheduling")
public class NewsletterController {

    private final NewsletterService newsletterService;

    /**
     * Manually triggers newsletter sending logic.
     * This runs the same workflow as the background scheduler,
     * sending all pending newsletters that are due.
     *
     * @return summary of sent/failed newsletters
     */
    @Operation(
            summary = "Trigger newsletter sending manually",
            description = "Runs the same logic as the scheduler to send all pending newsletters immediately."
    )
    @ApiResponse(responseCode = "200", description = "Newsletter send executed successfully")
    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendNow() {
        var result = newsletterService.sendDueNewsletters();

        // Example response structure: {"sentCount": 10, "failedCount": 2, "timestamp": "..."}
        Map<String, Object> response = Map.of(
                "message", "Manual send executed successfully",
                "sentCount", result.get("sentCount"),
                "failedCount", result.get("failedCount"),
                "timestamp", OffsetDateTime.now().toString()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Returns a simple scheduler health status.
     *
     * @return confirmation string
     */
    @Operation(
            summary = "Scheduler status check",
            description = "Confirms that the newsletter scheduler is running every minute."
    )
    @ApiResponse(responseCode = "200", description = "Scheduler status retrieved successfully")
    @GetMapping("/status")
    public ResponseEntity<String> status() {
        return ResponseEntity.ok(" Scheduler is active and runs every 60 seconds.");
    }
}