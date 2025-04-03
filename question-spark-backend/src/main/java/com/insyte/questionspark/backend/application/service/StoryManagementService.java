package com.insyte.questionspark.backend.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.insyte.questionspark.backend.application.port.in.StoryManagementUseCase;
import com.insyte.questionspark.backend.application.port.out.StoryRepositoryPort;
import com.insyte.questionspark.backend.domain.exception.ServiceException;
import com.insyte.questionspark.backend.domain.model.Story;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class StoryManagementService implements StoryManagementUseCase{

    private final StoryRepositoryPort storyRepositoryPort;

    public StoryManagementService(StoryRepositoryPort storyRepositoryPort) {
        this.storyRepositoryPort = storyRepositoryPort;
    }

    @Override
    public List<Story> getAllStories() throws ServiceException {
        try {
            return storyRepositoryPort.findAll();
        } catch (Exception e) {
            throw new ServiceException("Error fetching stories: " + e.getMessage() , e);
        }
    }
    
}
