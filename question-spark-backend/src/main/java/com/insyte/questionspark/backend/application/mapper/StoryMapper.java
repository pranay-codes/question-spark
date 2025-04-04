package com.insyte.questionspark.backend.application.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.insyte.questionspark.backend.domain.model.Story;
import com.insyte.questionspark.backend.infrastructure.adapter.rest.dto.StoryDetailDTO;
import com.insyte.questionspark.backend.infrastructure.adapter.rest.dto.StoryDto;

import java.util.Collections;
import java.util.Map;

public class StoryMapper {
    private StoryMapper() {
        // Private constructor to prevent instantiation
    }    
    public static StoryDto toDto(Story story) {
        if (story == null) {
            return null;
        }
        return new StoryDto(story.getId(), story.getTitle(), story.getDescription(), story.getCreatedAt());
    }
    
    public static Story toEntity(StoryDto storyDto) {
        if (storyDto == null) {
            return null;
        }
        Story story = new Story();
        story.setId(storyDto.id());
        story.setTitle(storyDto.title());
        story.setDescription(storyDto.description());
        story.setCreatedAt(storyDto.createdAt());
        return story;
    }

    public static StoryDetailDTO toDetailDTO(Story story) {
        StoryDetailDTO dto = new StoryDetailDTO(
            story.getId(), 
            story.getTitle(), 
            story.getDescription(), 
            story.getInitialPrompt(), 
            parseQuestionJson(story.getQuestions().get(0).getQuestionText()));
           
        return dto;
    }

    public static Map<String, Object> parseQuestionJson(String jsonString) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }
}
