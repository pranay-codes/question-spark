package com.insyte.questionspark.backend.application.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.insyte.questionspark.backend.application.port.out.StoryRepositoryPort;
import com.insyte.questionspark.backend.domain.exception.ServiceException;
import com.insyte.questionspark.backend.domain.exception.StoryNotFoundException;
import com.insyte.questionspark.backend.domain.model.Story;

@ExtendWith(MockitoExtension.class)
public class StoryManagementServiceTest {
    
    @Mock
    private StoryRepositoryPort storyRepositoryPort;

    private StoryManagementService storyManagementService;

    @BeforeEach
    void setUp() {
        storyManagementService = new StoryManagementService(storyRepositoryPort);
    }

    @Test
    void getAllStories_ShouldReturnAllStories_WhenStoriesExist() throws ServiceException {
        // Arrange
        Story story1 = createSampleStory("Test Story 1");
        Story story2 = createSampleStory("Test Story 2");
        List<Story> expectedStories = Arrays.asList(story1, story2);
        when(storyRepositoryPort.findAll()).thenReturn(expectedStories);

        // Act
        List<Story> actualStories = storyManagementService.getAllStories();

        // Assert
        assertThat(actualStories)
            .hasSize(2)
            .containsExactlyInAnyOrderElementsOf(expectedStories);
        verify(storyRepositoryPort).findAll();
    }

    @Test
    void getAllStories_ShouldReturnEmptyList_WhenNoStoriesExist() throws ServiceException {
        // Arrange
        when(storyRepositoryPort.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Story> actualStories = storyManagementService.getAllStories();

        // Assert
        assertThat(actualStories).isEmpty();
        verify(storyRepositoryPort).findAll();
    }

    @Test
    void getAllStories_ShouldPropagateException_WhenRepositoryFails() throws ServiceException {
        // Arrange
        when(storyRepositoryPort.findAll()).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThatThrownBy(() -> storyManagementService.getAllStories())
            .isInstanceOf(ServiceException.class)
            .hasMessage("Error fetching stories: Database error");
        verify(storyRepositoryPort).findAll();
    }

    @Test
    void getStoryWithQuestions_ShouldReturnStory_WhenStoryExists() throws ServiceException, StoryNotFoundException {
        // Arrange
        UUID storyId = UUID.randomUUID();
        Story expectedStory = new Story();
        expectedStory.setId(storyId);
        when(storyRepositoryPort.findById(storyId)).thenReturn(java.util.Optional.of(expectedStory));

        // Act
        Story result = storyManagementService.getStoryWithQuestions(storyId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(storyId);
        verify(storyRepositoryPort).findById(storyId);
    }

    @Test
    void getStoryWithQuestions_ShouldThrowNotFoundException_WhenStoryDoesNotExist() {
        // Arrange
        UUID storyId = UUID.randomUUID();
        when(storyRepositoryPort.findById(storyId)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> storyManagementService.getStoryWithQuestions(storyId))
            .isInstanceOf(StoryNotFoundException.class)
            .hasMessageContaining("Story not found with id: " + storyId);
    }

    @Test
    void getStoryWithQuestions_ShouldThrowServiceException_WhenRepositoryFails() {
        // Arrange
        UUID storyId = UUID.randomUUID();
        when(storyRepositoryPort.findById(storyId)).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThatThrownBy(() -> storyManagementService.getStoryWithQuestions(storyId))
            .isInstanceOf(ServiceException.class)
            .hasMessageContaining("Error fetching story with questions: Database error");
    }

    private Story createSampleStory(String title) {
        Story story = new Story();
        story.setId(UUID.randomUUID());
        story.setTitle(title);
        story.setDescription("Sample description");
        story.setInitialPrompt("Sample prompt");
        return story;
    }
}
