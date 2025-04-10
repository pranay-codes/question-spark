package com.insyte.questionspark.backend.application.service;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.insyte.questionspark.backend.application.port.in.StoryManagementUseCase;
import com.insyte.questionspark.backend.application.port.out.OpenAIServicePort;
import com.insyte.questionspark.backend.application.port.out.StoryQuestionRepositoryPort;
import com.insyte.questionspark.backend.application.port.out.StoryRepositoryPort;
import com.insyte.questionspark.backend.domain.exception.ServiceException;
import com.insyte.questionspark.backend.domain.exception.StoryNotFoundException;
import com.insyte.questionspark.backend.domain.model.Story;
import com.insyte.questionspark.backend.domain.model.StoryQuestion;
import com.insyte.questionspark.backend.infrastructure.adapter.openai.dto.StoryGenerationResponse;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class StoryManagementService implements StoryManagementUseCase {

    private static final Logger LOG = LoggerFactory.getLogger(StoryManagementService.class);

    private final StoryRepositoryPort storyRepositoryPort;
    private final OpenAIServicePort openAIService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public StoryManagementService(
        StoryRepositoryPort storyRepositoryPort,
        OpenAIServicePort openAIService) {
        this.storyRepositoryPort = storyRepositoryPort;
        this.openAIService = openAIService;
    }

    @Override
    public List<Story> getAllStories() throws ServiceException {
        try {
            return storyRepositoryPort.findAll();
        } catch (Exception e) {
            throw new ServiceException("Error fetching stories: " + e.getMessage() , e);
        }
    }

    @Override
    public Story getStoryWithQuestions(UUID storyId) throws ServiceException, StoryNotFoundException {
        try {
            return storyRepositoryPort.findById(storyId)
                .orElseThrow(() -> new StoryNotFoundException("Story not found with id: " + storyId));
        } catch (StoryNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Error fetching story with questions: " + e.getMessage(), e);
        }
    }

    @Override
    public UUID createStory(String initialPrompt) throws Exception {
        StoryGenerationResponse response = openAIService.generateStory(initialPrompt);

        Story story = new Story();
        story.setTitle(response.title());
        story.setDescription(response.description());
        story.setInitialPrompt(response.initialPrompt());

        StoryQuestion question = new StoryQuestion();
        String questionsJson = objectMapper.writeValueAsString(response.questions());
        JsonNode questionsJsonNode = objectMapper.readTree(questionsJson);

        question.setQuestionText(questionsJsonNode);
        question.setParentQuestion(null);
        question.setStory(story);
        story.addQuestion(question);
                
        return storyRepositoryPort.save(story).getId();
    }
}
