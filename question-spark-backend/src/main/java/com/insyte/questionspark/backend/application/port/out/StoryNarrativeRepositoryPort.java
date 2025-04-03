package com.insyte.questionspark.backend.application.port.out;

import com.insyte.questionspark.backend.domain.model.StoryNarrative;
import com.insyte.questionspark.backend.domain.model.Story;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

public interface StoryNarrativeRepositoryPort {
    StoryNarrative save(StoryNarrative narrative);
    Optional<StoryNarrative> findById(UUID id);
    List<StoryNarrative> findByStory(Story story);
    List<StoryNarrative> findByParentNarrativeId(UUID parentId);
    void deleteById(UUID id);
}