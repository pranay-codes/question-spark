package com.insyte.questionspark.backend.application.port.out;

import com.insyte.questionspark.backend.domain.model.StoryQuestion;
import com.insyte.questionspark.backend.domain.model.Story;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

// might not need this interface, as it is not used in the application layer
public interface StoryQuestionRepositoryPort {
    StoryQuestion save(StoryQuestion question);
    Optional<StoryQuestion> findById(UUID id);
    List<StoryQuestion> findByStory(Story story);
    List<StoryQuestion> findByParentQuestionId(UUID parentId);
    void deleteById(UUID id);
}