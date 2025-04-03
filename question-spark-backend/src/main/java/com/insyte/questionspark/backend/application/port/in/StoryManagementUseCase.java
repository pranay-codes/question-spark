package com.insyte.questionspark.backend.application.port.in;

import java.util.List;

import com.insyte.questionspark.backend.domain.exception.ServiceException;
import com.insyte.questionspark.backend.domain.model.Story;

public interface StoryManagementUseCase {
    List<Story> getAllStories() throws ServiceException;
}
