package com.acme.newsletter.controller;

import com.acme.newsletter.controller.SubscriberController;
import com.acme.newsletter.model.dto.CreateSubscriberRequest;
import com.acme.newsletter.model.dto.SubscriberResponse;
import com.acme.newsletter.service.SubscriberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SubscriberController.class)
class SubscriberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SubscriberService subscriberService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/subscribers → should create a new subscriber")
    void shouldCreateSubscriber() throws Exception {
        UUID id = UUID.randomUUID();
        UUID topicId = UUID.randomUUID();
        SubscriberResponse response = new SubscriberResponse(
                id,
                "user@example.com",
                true,
                OffsetDateTime.now()
        );

        Mockito.when(subscriberService.createSubscriber(eq("user@example.com"), eq(topicId)))
               .thenReturn(response);

        CreateSubscriberRequest request = new CreateSubscriberRequest();
        request.setEmail("user@example.com");
        request.setTopicId(topicId);

        mockMvc.perform(post("/api/subscribers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/subscribers/" + id))
                .andExpect(jsonPath("$.email").value("user@example.com"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @DisplayName("GET /api/subscribers → should return all subscribers")
    void shouldListAllSubscribers() throws Exception {
        SubscriberResponse s1 = new SubscriberResponse(UUID.randomUUID(), "a@x.com", true, OffsetDateTime.now());
        SubscriberResponse s2 = new SubscriberResponse(UUID.randomUUID(), "b@x.com", true, OffsetDateTime.now());

        Mockito.when(subscriberService.getAllSubscribers())
               .thenReturn(List.of(s1, s2));

        mockMvc.perform(get("/api/subscribers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].email").value("a@x.com"))
                .andExpect(jsonPath("$[1].email").value("b@x.com"));
    }

    @Test
    @DisplayName("GET /api/subscribers/topic/{topicId} → should return subscribers by topic")
    void shouldListSubscribersByTopic() throws Exception {
        UUID topicId = UUID.randomUUID();
        SubscriberResponse s1 = new SubscriberResponse(UUID.randomUUID(), "topic1@x.com", true, OffsetDateTime.now());
        SubscriberResponse s2 = new SubscriberResponse(UUID.randomUUID(), "topic2@x.com", true, OffsetDateTime.now());

        Mockito.when(subscriberService.getSubscribersByTopic(topicId))
               .thenReturn(List.of(s1, s2));

        mockMvc.perform(get("/api/subscribers/topic/{topicId}", topicId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].email").value("topic1@x.com"))
                .andExpect(jsonPath("$[1].email").value("topic2@x.com"));
    }

    @Test
    @DisplayName("DELETE /api/subscribers/{id} → should delete subscriber successfully")
    void shouldDeleteSubscriber() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.doNothing().when(subscriberService).deleteSubscriber(id);

        mockMvc.perform(delete("/api/subscribers/{id}", id))
                .andExpect(status().isNoContent());
    }
}
