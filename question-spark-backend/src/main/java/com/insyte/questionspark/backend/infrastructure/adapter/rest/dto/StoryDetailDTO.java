package com.insyte.questionspark.backend.infrastructure.adapter.rest.dto;

import java.util.Map;
import java.util.UUID;

public record StoryDetailDTO(UUID id, String title, String description, String initialPrompt, Map<String, Object> questions) {

    
}
