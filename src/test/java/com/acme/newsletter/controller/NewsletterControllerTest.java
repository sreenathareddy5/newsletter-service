package com.acme.newsletter.controller;

import com.acme.newsletter.controller.NewsletterController;
import com.acme.newsletter.service.NewsletterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NewsletterController.class)
class NewsletterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NewsletterService newsletterService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/newsletter/send → should trigger manual newsletter sending")
    void shouldTriggerManualSend() throws Exception {
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("sentCount", 8);
        mockResult.put("failedCount", 2);

        when(newsletterService.sendDueNewsletters()).thenReturn(mockResult);

        mockMvc.perform(post("/api/newsletter/send")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Manual send executed successfully"))
                .andExpect(jsonPath("$.sentCount").value(8))
                .andExpect(jsonPath("$.failedCount").value(2))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("GET /api/newsletter/status → should return scheduler status message")
    void shouldReturnSchedulerStatus() throws Exception {
        mockMvc.perform(get("/api/newsletter/status"))
                .andExpect(status().isOk())
                .andExpect(content().string(" Scheduler is active and runs every 60 seconds."));
    }
}
