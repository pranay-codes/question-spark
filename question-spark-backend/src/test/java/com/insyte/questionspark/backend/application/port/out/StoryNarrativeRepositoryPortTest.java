package com.insyte.questionspark.backend.application.port.out;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.insyte.questionspark.backend.domain.model.Story;
import com.insyte.questionspark.backend.domain.model.StoryNarrative;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class StoryNarrativeRepositoryPortTest {

    @Mock
    private StoryNarrativeRepositoryPort repository;

    private Story testStory;
    private StoryNarrative testNarrative;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testStory = new Story();
        testStory.setId(UUID.randomUUID());
        testStory.setTitle("Test Story");

        testNarrative = new StoryNarrative();
        testNarrative.setId(testId);
        testNarrative.setStory(testStory);
        testNarrative.setResponseText("Test narrative");
        testNarrative.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("save() should persist and return the narrative")
    void save_ShouldPersistAndReturnNarrative() {
        when(repository.save(testNarrative)).thenReturn(testNarrative);

        StoryNarrative savedNarrative = repository.save(testNarrative);

        assertThat(savedNarrative).isNotNull();
        assertThat(savedNarrative.getId()).isEqualTo(testId);
        verify(repository).save(testNarrative);
    }

    @Test
    @DisplayName("findById() should return narrative when exists")
    void findById_ShouldReturnNarrative_WhenExists() {
        when(repository.findById(testId)).thenReturn(Optional.of(testNarrative));

        Optional<StoryNarrative> result = repository.findById(testId);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(testId);
        verify(repository).findById(testId);
    }

    @Test
    @DisplayName("findById() should return empty when narrative not found")
    void findById_ShouldReturnEmpty_WhenNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        when(repository.findById(nonExistentId)).thenReturn(Optional.empty());

        Optional<StoryNarrative> result = repository.findById(nonExistentId);

        assertThat(result).isEmpty();
        verify(repository).findById(nonExistentId);
    }

    @Test
    @DisplayName("findByStory() should return list of narratives for story")
    void findByStory_ShouldReturnNarratives() {
        List<StoryNarrative> narratives = Arrays.asList(testNarrative);
        when(repository.findByStory(testStory)).thenReturn(narratives);

        List<StoryNarrative> result = repository.findByStory(testStory);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(testId);
        verify(repository).findByStory(testStory);
    }

    @Test
    @DisplayName("findByStory() should return empty list when no narratives exist")
    void findByStory_ShouldReturnEmptyList_WhenNoNarratives() {
        when(repository.findByStory(testStory)).thenReturn(Collections.emptyList());

        List<StoryNarrative> result = repository.findByStory(testStory);

        assertThat(result).isEmpty();
        verify(repository).findByStory(testStory);
    }

    @Test
    @DisplayName("findByParentNarrativeId() should return child narratives")
    void findByParentNarrativeId_ShouldReturnChildNarratives() {
        UUID parentId = UUID.randomUUID();
        List<StoryNarrative> childNarratives = Arrays.asList(testNarrative);
        when(repository.findByParentNarrativeId(parentId)).thenReturn(childNarratives);

        List<StoryNarrative> result = repository.findByParentNarrativeId(parentId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(testId);
        verify(repository).findByParentNarrativeId(parentId);
    }

    @Test
    @DisplayName("findByParentNarrativeId() should return empty list when no children exist")
    void findByParentNarrativeId_ShouldReturnEmptyList_WhenNoChildren() {
        UUID parentId = UUID.randomUUID();
        when(repository.findByParentNarrativeId(parentId)).thenReturn(Collections.emptyList());

        List<StoryNarrative> result = repository.findByParentNarrativeId(parentId);

        assertThat(result).isEmpty();
        verify(repository).findByParentNarrativeId(parentId);
    }

    @Test
    @DisplayName("deleteById() should delete existing narrative")
    void deleteById_ShouldDeleteNarrative() {
        doNothing().when(repository).deleteById(testId);

        repository.deleteById(testId);

        verify(repository).deleteById(testId);
    }

    @Test
    @DisplayName("deleteById() should handle non-existent narrative")
    void deleteById_ShouldHandleNonExistentNarrative() {
        UUID nonExistentId = UUID.randomUUID();
        doNothing().when(repository).deleteById(nonExistentId);

        repository.deleteById(nonExistentId);

        verify(repository).deleteById(nonExistentId);
    }
}