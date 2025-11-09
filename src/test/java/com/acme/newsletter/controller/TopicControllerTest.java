package com.acme.newsletter.controller;

import com.acme.newsletter.controller.TopicController;
import com.acme.newsletter.model.dto.CreateTopicRequest;
import com.acme.newsletter.model.dto.TopicResponse;
import com.acme.newsletter.service.TopicService;
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

@WebMvcTest(TopicController.class)
class TopicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TopicService topicService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/topics → should create a new topic")
    void shouldCreateTopic() throws Exception {
        TopicResponse response = new TopicResponse(
                UUID.randomUUID(),
                "Technology",
                "All about tech news",
                OffsetDateTime.now()
        );

        Mockito.when(topicService.createTopic(any(String.class), any(String.class)))
                .thenReturn(response);

        CreateTopicRequest request = new CreateTopicRequest();
        request.setName("Technology");
        request.setDescription("All about tech news");

        mockMvc.perform(post("/api/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/topics/" + response.id()))
                .andExpect(jsonPath("$.name").value("Technology"))
                .andExpect(jsonPath("$.description").value("All about tech news"));
    }

    @Test
    @DisplayName("GET /api/topics → should return list of topics")
    void shouldListTopics() throws Exception {
        TopicResponse topic1 = new TopicResponse(UUID.randomUUID(), "Tech", "Updates", OffsetDateTime.now());
        TopicResponse topic2 = new TopicResponse(UUID.randomUUID(), "Health", "Wellness", OffsetDateTime.now());

        Mockito.when(topicService.getAllTopics()).thenReturn(List.of(topic1, topic2));

        mockMvc.perform(get("/api/topics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Tech"))
                .andExpect(jsonPath("$[1].name").value("Health"));
    }

    @Test
    @DisplayName("GET /api/topics/{id} → should return single topic")
    void shouldGetTopicById() throws Exception {
        UUID id = UUID.randomUUID();
        TopicResponse topic = new TopicResponse(id, "Science", "Space", OffsetDateTime.now());

        Mockito.when(topicService.getTopicById(id)).thenReturn(topic);

        mockMvc.perform(get("/api/topics/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Science"))
                .andExpect(jsonPath("$.description").value("Space"));
    }

    @Test
    @DisplayName("DELETE /api/topics/{id} → should delete topic")
    void shouldDeleteTopic() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.doNothing().when(topicService).deleteTopic(id);

        mockMvc.perform(delete("/api/topics/{id}", id))
                .andExpect(status().isNoContent());
    }
}
