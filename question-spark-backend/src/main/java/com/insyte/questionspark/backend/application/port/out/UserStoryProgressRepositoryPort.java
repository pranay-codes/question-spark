package com.insyte.questionspark.backend.application.port.out;

import com.insyte.questionspark.backend.domain.model.UserStoryProgress;
import com.insyte.questionspark.backend.domain.model.Profile;
import com.insyte.questionspark.backend.domain.model.Story;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

public interface UserStoryProgressRepositoryPort {
    UserStoryProgress save(UserStoryProgress progress);
    Optional<UserStoryProgress> findById(UUID id);
    Optional<UserStoryProgress> findByUserAndStory(Profile user, Story story);
    List<UserStoryProgress> findByUser(Profile user);
    void deleteById(UUID id);
}