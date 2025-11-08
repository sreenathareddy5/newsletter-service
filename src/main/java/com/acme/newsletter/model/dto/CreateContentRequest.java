package com.acme.newsletter.model.dto;


import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class CreateContentRequest {
@NotNull
private UUID topicId;
@NotBlank
private String title;
@NotBlank
private String body;
@NotNull
private OffsetDateTime scheduledTime;
}