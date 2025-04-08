package com.insyte.questionspark.backend.application.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.insyte.questionspark.backend.domain.model.Story;
import com.insyte.questionspark.backend.domain.model.StoryQuestion;
import com.insyte.questionspark.backend.domain.model.StoryNarrative;
import com.insyte.questionspark.backend.infrastructure.adapter.rest.dto.StoryDetailDTO;
import com.insyte.questionspark.backend.infrastructure.adapter.rest.dto.StoryDTO;
import com.insyte.questionspark.backend.infrastructure.adapter.rest.dto.StoryNarrativeDTO;

import java.util.Collections;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class StoryMapper {

    public StoryDTO toDto(Story story) {
        if (story == null) {
            return null;
        }
        return new StoryDTO(story.getId(), story.getTitle(), story.getDescription(), story.getCreatedAt());
    }

    public Story toEntity(StoryDTO storyDto) {
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

    public StoryDetailDTO toDetailDTO(Story story) {
        if (story == null) {
            return null;
        }
        return new StoryDetailDTO(
            story.getId(),
            story.getTitle(),
            story.getDescription(),
            story.getInitialPrompt(),
            story.getQuestions() != null && !story.getQuestions().isEmpty() ? story.getQuestions().get(0).getId() : null,
            story.getQuestions() != null && !story.getQuestions().isEmpty() ? story.getQuestions().get(0).getQuestionText() : null
        );
    }

    public StoryNarrativeDTO toNarrativeDTO(StoryNarrative narrative) {
        if (narrative == null) {
            return null;
        }
        return new StoryNarrativeDTO(
            narrative.getId(),
            null,
            "narrative.getContent()",
            narrative.getCreatedAt()
        );
    }
}
