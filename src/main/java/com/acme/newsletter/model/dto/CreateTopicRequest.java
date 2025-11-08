package com.acme.newsletter.model.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class CreateTopicRequest {
@NotBlank
private String name;
private String description;
}