package com.acme.newsletter.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "The core Topic entity, representing a category for content segregation.")
public class Topic {
    
    @Schema(description = "Unique identifier for the Topic.", example = "101")
    private Long id;
    
    @Schema(description = "User-friendly name of the topic (e.g., 'Daily Tech').", example = "Tech News", required = true)
    private String name;
    
    @Schema(description = "A brief description of the topic's focus.", example = "Latest developments in AI, gadgets, and software.")
    private String description;
    
    // Constructors (omitted for brevity, but assumed via Lombok or manual code)
    // You would include standard getters and setters.
}