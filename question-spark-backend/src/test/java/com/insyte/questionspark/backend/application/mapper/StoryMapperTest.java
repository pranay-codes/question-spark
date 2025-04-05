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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

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

    @Test
    @DisplayName("Should parse complex JSON question structure")
    void parseQuestionJson_ShouldHandleComplexJson() {
        // Arrange
        StoryQuestion question = new StoryQuestion();
        question.setQuestionText(COMPLEX_JSON);

        // Act
        Map<String, Object> result = StoryMapper.parseQuestionJson(question.getQuestionText());

        // Assert
        assertThat(result).isNotEmpty();
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> questions = (List<Map<String, Object>>) result.get("questions");
        assertThat(questions).hasSize(1);
        
        Map<String, Object> firstQuestion = questions.get(0);
        
        // Verify question structure
        @SuppressWarnings("unchecked")
        Map<String, Object> questionData = (Map<String, Object>) firstQuestion.get("question");
        assertThat(questionData)
            .containsEntry("type", "text")
            .containsEntry("content", "What should the robot do after finding the puppy?");
        
        // Verify response structure
        @SuppressWarnings("unchecked")
        Map<String, Object> responseData = (Map<String, Object>) firstQuestion.get("response");
        assertThat(responseData)
            .containsEntry("type", "generated")
            .containsEntry("content", "The robot gently picks up the puppy and looks around.");
        
        // Verify follow_ups structure
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> followUps = (List<Map<String, Object>>) firstQuestion.get("follow_ups");
        assertThat(followUps).hasSize(2);
        
        // Verify first follow-up
        assertThat(followUps.get(0))
            .containsEntry("action", "Search for the puppy's owner");
    }

    @Test
    @DisplayName("Should handle null JSON string")
    void parseQuestionJson_ShouldHandleNullInput() {
        // Act
        Map<String, Object> result = StoryMapper.parseQuestionJson(null);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should handle invalid JSON string")
    void parseQuestionJson_ShouldHandleInvalidJson() {
        // Arrange
        String invalidJson = "{ invalid json }";

        // Act
        Map<String, Object> result = StoryMapper.parseQuestionJson(invalidJson);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should map Story to StoryDto correctly")
    void toDto_ShouldMapAllFields() {
        // Arrange
        Story story = new Story();
        story.setId(UUID.randomUUID());
        story.setTitle("Test Story");
        story.setDescription("Test Description");
        story.setCreatedAt(LocalDateTime.now());

        // Act
        StoryDTO dto = StoryMapper.toDto(story);

        // Assert
        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(story.getId());
        assertThat(dto.title()).isEqualTo(story.getTitle());
        assertThat(dto.description()).isEqualTo(story.getDescription());
        assertThat(dto.createdAt()).isEqualTo(story.getCreatedAt());
    }

    @Test
    @DisplayName("Should map Story to StoryDetailDTO with questions")
    void toDetailDTO_ShouldMapWithQuestions() {
        // Arrange
        Story story = new Story();
        story.setId(UUID.randomUUID());
        story.setTitle("Test Story");
        story.setDescription("Test Description");
        story.setInitialPrompt("Initial prompt");

        StoryQuestion question = new StoryQuestion();
        question.setQuestionText(COMPLEX_JSON);
        story.setQuestions(new ArrayList<>(List.of(question)));

        // Act
        StoryDetailDTO detailDTO = StoryMapper.toDetailDTO(story);

        // Assert
        assertThat(detailDTO).isNotNull();
        assertThat(detailDTO.id()).isEqualTo(story.getId());
        assertThat(detailDTO.title()).isEqualTo(story.getTitle());
        assertThat(detailDTO.description()).isEqualTo(story.getDescription());
        assertThat(detailDTO.initialPrompt()).isEqualTo(story.getInitialPrompt());
        assertThat(detailDTO.questions()).isNotEmpty();
    }
}