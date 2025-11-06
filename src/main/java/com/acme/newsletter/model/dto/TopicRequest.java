package com.acme.newsletter.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@Schema(description = "Request body for creating or updating a Topic.")
public class TopicRequest {
    
    @NotBlank(message = "Topic name is required.")
    @Size(min = 3, max = 100)
    @Schema(description = "The name of the topic.", example = "Finance Updates", required = true)
    private String name;
    
    @Schema(description = "A brief description of the topic.", example = "Weekly market analysis and investment tips.")
    private String description;
}