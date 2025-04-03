package com.insyte.questionspark.backend.application.port.out;

import com.insyte.questionspark.backend.domain.model.Story;
import com.insyte.questionspark.backend.domain.model.Profile;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

public interface StoryRepositoryPort {
    Story save(Story story);
    List<Story> findAll();
    Optional<Story> findById(UUID id);
    List<Story> findByAuthor(Profile author);
    void deleteById(UUID id);
}