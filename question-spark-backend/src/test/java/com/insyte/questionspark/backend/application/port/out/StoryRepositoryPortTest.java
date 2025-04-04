package com.insyte.questionspark.backend.application.port.out;

import com.insyte.questionspark.backend.domain.model.Story;
import com.insyte.questionspark.backend.domain.model.Profile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Arrays;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoryRepositoryPortTest {

    @Mock
    private StoryRepositoryPort storyRepositoryPort;

    private Story testStory;
    private Profile testAuthor;

    @BeforeEach
    void setUp() {
        testAuthor = new Profile();
        testAuthor.setId(UUID.randomUUID());
        testAuthor.setUsername("testUser");

        testStory = new Story();
        testStory.setId(UUID.randomUUID());
        testStory.setTitle("Test Story");
        testStory.setDescription("Test Description");
        testStory.setAuthor(testAuthor);
        testStory.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should save story successfully")
    void save_ShouldReturnSavedStory() {
        // Arrange
        when(storyRepositoryPort.save(testStory)).thenReturn(testStory);

        // Act
        Story savedStory = storyRepositoryPort.save(testStory);

        // Assert
        assertThat(savedStory).isNotNull();
        assertThat(savedStory.getId()).isEqualTo(testStory.getId());
        verify(storyRepositoryPort).save(testStory);
    }

    @Test
    @DisplayName("Should find all stories")
    void findAll_ShouldReturnAllStories() {
        // Arrange
        Story story2 = new Story();
        story2.setId(UUID.randomUUID());
        story2.setTitle("Test Story 2");
        
        when(storyRepositoryPort.findAll()).thenReturn(Arrays.asList(testStory, story2));

        // Act
        List<Story> stories = storyRepositoryPort.findAll();

        // Assert
        assertThat(stories)
            .isNotNull()
            .hasSize(2)
            .contains(testStory, story2);
        verify(storyRepositoryPort).findAll();
    }

    @Test
    @DisplayName("Should find story by ID when exists")
    void findById_ShouldReturnStory_WhenExists() {
        // Arrange
        when(storyRepositoryPort.findById(testStory.getId())).thenReturn(Optional.of(testStory));

        // Act
        Optional<Story> found = storyRepositoryPort.findById(testStory.getId());

        // Assert
        assertThat(found)
            .isPresent()
            .contains(testStory);
        verify(storyRepositoryPort).findById(testStory.getId());
    }

    @Test
    @DisplayName("Should return empty when story ID doesn't exist")
    void findById_ShouldReturnEmpty_WhenNotExists() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(storyRepositoryPort.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act
        Optional<Story> found = storyRepositoryPort.findById(nonExistentId);

        // Assert
        assertThat(found).isEmpty();
        verify(storyRepositoryPort).findById(nonExistentId);
    }

    @Test
    @DisplayName("Should find stories by author")
    void findByAuthor_ShouldReturnAuthorStories() {
        // Arrange
        when(storyRepositoryPort.findByAuthor(testAuthor)).thenReturn(List.of(testStory));

        // Act
        List<Story> authorStories = storyRepositoryPort.findByAuthor(testAuthor);

        // Assert
        assertThat(authorStories)
            .isNotNull()
            .hasSize(1)
            .contains(testStory);
        verify(storyRepositoryPort).findByAuthor(testAuthor);
    }

    @Test
    @DisplayName("Should delete story by ID")
    void deleteById_ShouldDeleteStory() {
        // Arrange
        UUID storyId = testStory.getId();
        doNothing().when(storyRepositoryPort).deleteById(storyId);

        // Act
        storyRepositoryPort.deleteById(storyId);

        // Assert
        verify(storyRepositoryPort).deleteById(storyId);
    }
}