package com.insyte.questionspark.backend.application.mapper;

import com.insyte.questionspark.backend.domain.model.Story;
import com.insyte.questionspark.backend.infrastructure.adapter.rest.dto.StoryDto;

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
}
