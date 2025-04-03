package com.insyte.questionspark.backend.application.port.out;

import com.insyte.questionspark.backend.domain.model.StoryQuestion;
import com.insyte.questionspark.backend.domain.model.Story;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

public interface StoryQuestionRepositoryPort {
    StoryQuestion save(StoryQuestion question);
    Optional<StoryQuestion> findById(UUID id);
    List<StoryQuestion> findByStory(Story story);
    List<StoryQuestion> findByParentQuestionId(UUID parentId);
    void deleteById(UUID id);
}