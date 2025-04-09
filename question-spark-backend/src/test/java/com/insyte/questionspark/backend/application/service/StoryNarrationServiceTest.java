package com.insyte.questionspark.backend.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.insyte.questionspark.backend.application.port.out.ProfileRepositoryPort;
import com.insyte.questionspark.backend.application.port.out.StoryNarrativeRepositoryPort;
import com.insyte.questionspark.backend.application.port.out.StoryRepositoryPort;
import com.insyte.questionspark.backend.domain.exception.ServiceException;
import com.insyte.questionspark.backend.domain.exception.StoryNotFoundException;
import com.insyte.questionspark.backend.domain.model.Profile;
import com.insyte.questionspark.backend.domain.model.Story;
import com.insyte.questionspark.backend.domain.model.StoryNarrative;
import com.insyte.questionspark.backend.domain.model.StoryQuestion;
import com.insyte.questionspark.backend.infrastructure.adapter.rest.dto.CreateNarrationRequest;

@ExtendWith(MockitoExtension.class)
class StoryNarrationServiceTest {

    @Mock
    private StoryRepositoryPort storyRepositoryPort;

    @Mock
    private StoryNarrativeRepositoryPort storyNarrativeRepositoryPort;

    @Mock
    private ProfileRepositoryPort profileRepositoryPort;

    @InjectMocks
    private StoryNarrationService storyNarrationService;

    private UUID storyId;
    private UUID questionId;
    private UUID profileId;
    private Story story;
    private StoryQuestion question;
    private Profile profile;
    private CreateNarrationRequest request;

    private JsonNode nextNarrativeNode;

    @BeforeEach
    void setUp() {
        storyId = UUID.randomUUID();
        questionId = UUID.randomUUID();
        profileId = UUID.randomUUID();

        // Create test story
        story = new Story();
        story.setId(storyId);
        story.setTitle("Test Story");

        // Create test question
        question = new StoryQuestion();
        question.setId(questionId);
        ObjectMapper objectMapper = new ObjectMapper();
        question.setQuestionText(objectMapper.createObjectNode().put("text", "Test Question"));
        story.setQuestions(Arrays.asList(question));

        // Create test profile
        profile = new Profile();
        profile.setId(profileId);

        // Create test request
        nextNarrativeNode = objectMapper.createObjectNode().put("key", "value");

        request = new CreateNarrationRequest(
            null,
            questionId,
            "Updated Question Text",
            "Updated Response Text",
            "Updated Action",
            nextNarrativeNode,
            profileId.toString()
        );
    }

    @Test
    @DisplayName("Should create narration successfully when all data is valid")
    void createNarration_ShouldSucceed_WhenAllDataIsValid() throws ServiceException, StoryNotFoundException {
        // Arrange
        when(storyRepositoryPort.findById(storyId)).thenReturn(Optional.of(story));
        when(profileRepositoryPort.findById(profileId)).thenReturn(Optional.of(profile));
        when(storyNarrativeRepositoryPort.save(any())).thenAnswer(i -> i.getArgument(0));

        // Act
        StoryNarrative result = storyNarrationService.createNarration(storyId, request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getStory()).isEqualTo(story);
        assertThat(result.getQuestion()).isEqualTo(question);
        assertThat(result.getChoiceText()).isEqualTo(request.questionText());
        assertThat(result.getResponseText()).isEqualTo(request.response());
        assertThat(result.getNextNarrative()).isEqualTo(request.nextNarrative());
        assertThat(result.getUser()).isEqualTo(profile);
        
        verify(storyRepositoryPort).findById(storyId);
        verify(profileRepositoryPort).findById(profileId);
        verify(storyNarrativeRepositoryPort).save(any(StoryNarrative.class));
    }

    @Test
    @DisplayName("Should throw StoryNotFoundException when story doesn't exist")
    void createNarration_ShouldThrowException_WhenStoryNotFound() {
        // Arrange
        when(storyRepositoryPort.findById(storyId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> storyNarrationService.createNarration(storyId, request))
            .isInstanceOf(StoryNotFoundException.class)
            .hasMessageContaining("Story not found");
    }

    @Test
    @DisplayName("Should throw ServiceException when question doesn't exist in story")
    void createNarration_ShouldThrowException_WhenQuestionNotFound() {
        // Arrange
        request = new CreateNarrationRequest(
            UUID.randomUUID(), 
            UUID.randomUUID(), // Different question ID
            "Question Text",
            "Response Text",
            "Action",
            nextNarrativeNode,
            profileId.toString()
        );
        
        when(storyRepositoryPort.findById(storyId)).thenReturn(Optional.of(story));

        // Act & Assert
        assertThatThrownBy(() -> storyNarrationService.createNarration(storyId, request))
            .isInstanceOf(ServiceException.class)
            .hasMessageContaining("Question not found");
    }

    @Test
    @DisplayName("Should throw ServiceException when user profile doesn't exist")
    void createNarration_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(storyRepositoryPort.findById(storyId)).thenReturn(Optional.of(story));
        when(profileRepositoryPort.findById(any())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> storyNarrationService.createNarration(storyId, request))
            .isInstanceOf(ServiceException.class)
            .hasMessageContaining("User not found");
    }

    @Test
    @DisplayName("Should set parent narrative when parentNarrativeId is provided")
    void createNarration_ShouldSetParentNarrative_WhenParentIdProvided() throws ServiceException, StoryNotFoundException {
        // Arrange
        UUID parentNarrativeId = UUID.randomUUID();
        StoryNarrative parentNarrative = new StoryNarrative();
        parentNarrative.setId(parentNarrativeId);

        request = new CreateNarrationRequest(
            parentNarrativeId,
            questionId,
            "Question Text",
            "Response Text",
            "Action",
            nextNarrativeNode,
            profileId.toString()
        );

        when(storyRepositoryPort.findById(storyId)).thenReturn(Optional.of(story));
        when(profileRepositoryPort.findById(profileId)).thenReturn(Optional.of(profile));
        when(storyNarrativeRepositoryPort.findById(parentNarrativeId))
            .thenReturn(Optional.of(parentNarrative));
        when(storyNarrativeRepositoryPort.save(any())).thenAnswer(i -> i.getArgument(0));

        // Act
        StoryNarrative result = storyNarrationService.createNarration(storyId, request);

        // Assert
        assertThat(result.getParentNarrative()).isEqualTo(parentNarrative);
        verify(storyNarrativeRepositoryPort).findById(parentNarrativeId);
    }

    @Test
    @DisplayName("Should throw ServiceException when unexpected error occurs")
    void createNarration_ShouldThrowServiceException_WhenUnexpectedErrorOccurs() {
        // Arrange
        when(storyRepositoryPort.findById(storyId)).thenReturn(Optional.of(story));
        when(profileRepositoryPort.findById(profileId)).thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        assertThatThrownBy(() -> storyNarrationService.createNarration(storyId, request))
            .isInstanceOf(ServiceException.class)
            .hasMessageContaining("Error creating narration")
            .hasCauseInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Should set creation and update timestamps")
    void createNarration_ShouldSetTimestamps() throws ServiceException, StoryNotFoundException {
        // Arrange
        when(storyRepositoryPort.findById(storyId)).thenReturn(Optional.of(story));
        when(profileRepositoryPort.findById(profileId)).thenReturn(Optional.of(profile));
        when(storyNarrativeRepositoryPort.save(any())).thenAnswer(i -> i.getArgument(0));

        // Act
        StoryNarrative result = storyNarrationService.createNarration(storyId, request);

        // Assert
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isNotNull();
        assertThat(result.getCreatedAt()).isEqualToIgnoringSeconds(LocalDateTime.now());
        assertThat(result.getUpdatedAt()).isEqualToIgnoringSeconds(LocalDateTime.now());
    }
}