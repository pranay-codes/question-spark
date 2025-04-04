package com.insyte.questionspark.backend.application.port.in;

import java.util.List;
import java.util.UUID;

import com.insyte.questionspark.backend.domain.exception.ServiceException;
import com.insyte.questionspark.backend.domain.exception.StoryNotFoundException;
import com.insyte.questionspark.backend.domain.model.Story;

public interface StoryManagementUseCase {
    List<Story> getAllStories() throws ServiceException;
    Story getStoryWithQuestions(UUID storyId) throws ServiceException, StoryNotFoundException;
}
