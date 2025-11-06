package com.acme.newsletter.model.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ContentCreationRequest {
    @NotNull(message = "Topic ID is required")
    private Long topicId;
    
    @NotBlank(message = "Subject is required")
    private String subject;
    
    @NotBlank(message = "Body content is required")
    private String body;

    // Content must be scheduled for today or a future date
    @NotNull(message = "Scheduled date is required")
    @FutureOrPresent(message = "Scheduled date must be today or in the future")
    private LocalDate scheduledForDate;
}