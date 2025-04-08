package com.insyte.questionspark.backend.application.mapper;

import com.insyte.questionspark.backend.domain.model.Story;
import com.insyte.questionspark.backend.domain.model.StoryQuestion;
import com.insyte.questionspark.backend.infrastructure.adapter.rest.dto.StoryDTO;
import com.insyte.questionspark.backend.infrastructure.adapter.rest.dto.StoryDetailDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class StoryMapperTest {

    private static final String COMPLEX_JSON = """
        {
          "questions": [
            {
              "question": {
                "type": "text",
                "content": "What should the robot do after finding the puppy?"
              },
              "response": {
                "type": "generated",
                "content": "The robot gently picks up the puppy and looks around."
              },
              "follow_ups": [
                {
                  "action": "Search for the puppy's owner",
                  "narrative": {
                    "id": "narrative-001",
                    "content": "The robot walks around the park, asking other visitors if they've seen a missing puppy flyer."
                  }
                },
                {
                  "action": "Take the puppy home",
                  "narrative": {
                    "id": "narrative-002",
                    "content": "The robot cradles the puppy and heads back to its lab, making a cozy bed from spare circuit cushions."
                  }
                }
              ]
            }
          ]
        }
        """;


    StoryMapper storyMapper = mock(StoryMapper.class);


    @Test
    @DisplayName("Should map Story to StoryDto correctly")
    void toDto_ShouldMapAllFields() {



        // Arrange
        Story story = new Story();
        story.setId(UUID.randomUUID());
        story.setTitle("Test Story");
        story.setDescription("Test Description");
        story.setCreatedAt(LocalDateTime.now());

        StoryQuestion question = new StoryQuestion();
        question.setId(UUID.randomUUID());  
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode questionJsonNode = objectMapper.valueToTree(Map.of("question", COMPLEX_JSON));
        question.setQuestionText(questionJsonNode);

        
        when(storyMapper.toDetailDTO(story)).thenReturn(new StoryDetailDTO(
            story.getId(),
            story.getTitle(),
            story.getDescription(),
            story.getInitialPrompt(),
            question.getId(),
            question.getQuestionText()
        ));

        // Act
        StoryDetailDTO dto = storyMapper.toDetailDTO(story);

        // Assert
        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(story.getId());
        assertThat(dto.title()).isEqualTo(story.getTitle());
        assertThat(dto.description()).isEqualTo(story.getDescription());
        assertThat(dto.initialPrompt()).isEqualTo(story.getInitialPrompt());
        assertThat(dto.questionId()).isEqualTo(question.getId());
    }

    @Test
    @DisplayName("Should map Story to StoryDetailDTO with questions")
    void toDetailDTO_ShouldMapWithQuestions() throws JsonMappingException, JsonProcessingException {
        // Arrange
        Story story = new Story();
        story.setId(UUID.randomUUID());
        story.setTitle("Test Story");
        story.setDescription("Test Description");
        story.setInitialPrompt("Initial prompt");
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(COMPLEX_JSON);
        StoryQuestion question = new StoryQuestion();
        question.setId(UUID.randomUUID());
        question.setQuestionText(jsonNode);
        story.setQuestions(new ArrayList<>(List.of(question)));

        when(storyMapper.toDetailDTO(story)).thenReturn(new StoryDetailDTO(
            story.getId(),
            story.getTitle(),
            story.getDescription(),
            story.getInitialPrompt(),
            question.getId(),
            question.getQuestionText()
        ));

        // Act
        StoryDetailDTO detailDTO = storyMapper.toDetailDTO(story);

        // Assert
        assertThat(detailDTO).isNotNull();
        assertThat(detailDTO.id()).isEqualTo(story.getId());
        assertThat(detailDTO.title()).isEqualTo(story.getTitle());
        assertThat(detailDTO.description()).isEqualTo(story.getDescription());
        assertThat(detailDTO.initialPrompt()).isEqualTo(story.getInitialPrompt());
        assertThat(detailDTO.questionId()).isEqualTo(question.getId());
        assertThat(detailDTO.question()).isEqualTo(question.getQuestionText());
    }


}