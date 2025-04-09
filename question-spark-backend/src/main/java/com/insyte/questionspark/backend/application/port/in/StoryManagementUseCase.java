package com.insyte.questionspark.backend.application.port.in;

import java.util.List;
import java.util.UUID;
import com.insyte.questionspark.backend.domain.model.Story;

public interface StoryManagementUseCase {
    List<Story> getAllStories() throws Exception;
    Story getStoryWithQuestions(UUID storyId) throws Exception;
    UUID createStory(String initialPrompt) throws Exception;
}
