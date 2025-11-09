package com.acme.newsletter.controller;

import com.acme.newsletter.model.dto.ContentResponse;
import com.acme.newsletter.model.dto.CreateContentRequest;
import com.acme.newsletter.service.ContentService;
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
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContentController.class)
class ContentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContentService contentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/content → should create new content successfully")
    void shouldCreateContent() throws Exception {
        UUID id = UUID.randomUUID();
        UUID topicId = UUID.randomUUID();
        OffsetDateTime schedule = OffsetDateTime.now().plusDays(1);

        ContentResponse response = new ContentResponse(
                id,
                "Weekly Update",
                "Content body text",
                schedule,
                "SCHEDULED",
                topicId,
                "Technology"
        );

        Mockito.when(contentService.createContent(eq(topicId), eq("Weekly Update"), eq("Content body text"), eq(schedule)))
               .thenReturn(response);

        CreateContentRequest request = new CreateContentRequest();
        request.setTopicId(topicId);
        request.setTitle("Weekly Update");
        request.setBody("Content body text");
        request.setScheduledTime(schedule);

        mockMvc.perform(post("/api/content")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/content/" + id))
                .andExpect(jsonPath("$.title").value("Weekly Update"))
                .andExpect(jsonPath("$.status").value("SCHEDULED"))
                .andExpect(jsonPath("$.topicName").value("Technology"));
    }

    @Test
    @DisplayName("GET /api/content → should return all content items")
    void shouldListAllContent() throws Exception {
        ContentResponse c1 = new ContentResponse(UUID.randomUUID(), "Post1", "Body1", OffsetDateTime.now(), "SENT", UUID.randomUUID(), "Topic1");
        ContentResponse c2 = new ContentResponse(UUID.randomUUID(), "Post2", "Body2", OffsetDateTime.now(), "SCHEDULED", UUID.randomUUID(), "Topic2");

        Mockito.when(contentService.getAllContent()).thenReturn(List.of(c1, c2));

        mockMvc.perform(get("/api/content"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Post1"))
                .andExpect(jsonPath("$[1].status").value("SCHEDULED"));
    }

    @Test
    @DisplayName("GET /api/content/{id} → should return content by ID")
    void shouldGetContentById() throws Exception {
        UUID id = UUID.randomUUID();
        ContentResponse response = new ContentResponse(
                id,
                "Daily News",
                "Today’s highlights...",
                OffsetDateTime.now(),
                "SENT",
                UUID.randomUUID(),
                "News"
        );

        Mockito.when(contentService.getContentById(id)).thenReturn(Optional.of(response));

        mockMvc.perform(get("/api/content/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Daily News"))
                .andExpect(jsonPath("$.status").value("SENT"))
                .andExpect(jsonPath("$.topicName").value("News"));
    }

    @Test
    @DisplayName("GET /api/content/{id} → should return 404 when content not found")
    void shouldReturnNotFoundForMissingContent() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.when(contentService.getContentById(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/content/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/content/{id} → should return 400 for invalid UUID")
    void shouldReturnBadRequestForInvalidUUID() throws Exception {
        mockMvc.perform(get("/api/content/invalid-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/content/{id} → should delete content successfully")
    void shouldDeleteContent() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.doNothing().when(contentService).deleteContent(id);

        mockMvc.perform(delete("/api/content/{id}", id))
                .andExpect(status().isNoContent());
    }
}
