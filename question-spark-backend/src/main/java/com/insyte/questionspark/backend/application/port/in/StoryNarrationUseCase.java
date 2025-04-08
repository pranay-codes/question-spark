package com.insyte.questionspark.backend.application.port.in;

import java.util.UUID;
import com.insyte.questionspark.backend.domain.model.StoryNarrative;
import com.insyte.questionspark.backend.domain.exception.ServiceException;
import com.insyte.questionspark.backend.domain.exception.StoryNotFoundException;
import com.insyte.questionspark.backend.infrastructure.adapter.rest.dto.CreateNarrationRequest;

public interface StoryNarrationUseCase {
    StoryNarrative createNarration(UUID storyId, CreateNarrationRequest request) 
        throws ServiceException, StoryNotFoundException;
}