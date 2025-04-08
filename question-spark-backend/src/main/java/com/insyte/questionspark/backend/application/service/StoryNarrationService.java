package com.insyte.questionspark.backend.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

import com.insyte.questionspark.backend.application.port.in.StoryNarrationUseCase;
import com.insyte.questionspark.backend.application.port.out.StoryRepositoryPort;
import com.insyte.questionspark.backend.application.port.out.ProfileRepositoryPort;
import com.insyte.questionspark.backend.application.port.out.StoryNarrativeRepositoryPort;
import com.insyte.questionspark.backend.domain.model.StoryNarrative;
import com.insyte.questionspark.backend.domain.exception.ServiceException;
import com.insyte.questionspark.backend.domain.exception.StoryNotFoundException;
import com.insyte.questionspark.backend.infrastructure.adapter.rest.dto.CreateNarrationRequest;

@Service
@Transactional
public class StoryNarrationService implements StoryNarrationUseCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(StoryNarrationService.class);
    
    private final StoryRepositoryPort storyRepositoryPort;
    private final StoryNarrativeRepositoryPort storyNarrativeRepositoryPort;
    private final ProfileRepositoryPort profileRepositoryPort;

    public StoryNarrationService(
        StoryRepositoryPort storyRepositoryPort,
        StoryNarrativeRepositoryPort storyNarrativeRepositoryPort,
        ProfileRepositoryPort profileRepositoryPort
    ) {
        this.storyRepositoryPort = storyRepositoryPort;
        this.storyNarrativeRepositoryPort = storyNarrativeRepositoryPort;
        this.profileRepositoryPort = profileRepositoryPort;
    }

    @Override
    public StoryNarrative createNarration(UUID storyId, CreateNarrationRequest request) 
        throws ServiceException, StoryNotFoundException {
        try {
            var story = storyRepositoryPort.findById(storyId)
                .orElseThrow(() -> new StoryNotFoundException("Story not found with id: " + storyId));

            var narrative = new StoryNarrative();
            narrative.setStory(story);
            if (request.parentNarrativeId() != null) {
                storyNarrativeRepositoryPort.findById(request.parentNarrativeId())
                    .ifPresent(narrative::setParentNarrative);
            }
            narrative.setChoiceText(request.questionText());
            narrative.setResponseText(request.response());
            narrative.setNextNarrative(request.nextNarrative());
            narrative.setCreatedAt(LocalDateTime.now());
            narrative.setUpdatedAt(LocalDateTime.now());
            
            narrative.setUser(profileRepositoryPort.findById(UUID.fromString(request.profileId()) )
                .orElseThrow(() -> new ServiceException("User not found with id: " + "ff43d36d-1d49-4827-8c2a-ed6ee6390e2e")));
            
            LOGGER.info("Creating narration for story: {}", storyId);
            return storyNarrativeRepositoryPort.save(narrative);
        } catch (StoryNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Error creating narration: " + e.getMessage(), e);
        }
    }
}