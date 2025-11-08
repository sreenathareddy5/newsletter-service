package com.acme.newsletter.model.dto;
import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class CreateSubscriberRequest {
@Email
@NotNull
private String email;
@NotNull
private UUID topicId;
}