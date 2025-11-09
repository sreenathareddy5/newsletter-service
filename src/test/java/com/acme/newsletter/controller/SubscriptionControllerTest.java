package com.acme.newsletter.controller;

import com.acme.newsletter.model.dto.SubscriptionResponse;
import com.acme.newsletter.service.SubscriptionService;
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

@WebMvcTest(SubscriptionController.class)
class SubscriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SubscriptionService subscriptionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/subscriptions → should return list of subscriptions")
    void shouldListSubscriptions() throws Exception {
        SubscriptionResponse sub1 = new SubscriptionResponse(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Technology",
                OffsetDateTime.now()
        );

        SubscriptionResponse sub2 = new SubscriptionResponse(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Health",
                OffsetDateTime.now()
        );

        Mockito.when(subscriptionService.getAllSubscriptions())
                .thenReturn(List.of(sub1, sub2));

        mockMvc.perform(get("/api/subscriptions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].topicName").value("Technology"))
                .andExpect(jsonPath("$[1].topicName").value("Health"));
    }

    @Test
    @DisplayName("POST /api/subscriptions/{subscriberId}/{topicId} → should create a subscription")
    void shouldCreateSubscription() throws Exception {
        UUID subscriberId = UUID.randomUUID();
        UUID topicId = UUID.randomUUID();

        SubscriptionResponse response = new SubscriptionResponse(
                subscriberId,
                topicId,
                "AI & Machine Learning",
                OffsetDateTime.now()
        );

        Mockito.when(subscriptionService.subscribe(eq(subscriberId), eq(topicId)))
                .thenReturn(response);

        mockMvc.perform(post("/api/subscriptions/{subscriberId}/{topicId}", subscriberId, topicId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.subscriberId").value(subscriberId.toString()))
                .andExpect(jsonPath("$.topicId").value(topicId.toString()))
                .andExpect(jsonPath("$.topicName").value("AI & Machine Learning"));
    }

    @Test
    @DisplayName("DELETE /api/subscriptions/{subscriberId}/{topicId} → should unsubscribe successfully")
    void shouldUnsubscribe() throws Exception {
        UUID subscriberId = UUID.randomUUID();
        UUID topicId = UUID.randomUUID();

        Mockito.doNothing().when(subscriptionService).unsubscribe(subscriberId, topicId);

        mockMvc.perform(delete("/api/subscriptions/{subscriberId}/{topicId}", subscriberId, topicId))
                .andExpect(status().isNoContent());
    }
}
