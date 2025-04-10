package com.insyte.questionspark.backend.infrastructure.adapter.openai.dto;

import java.util.List;


public record StoryGenerationResponse(
    String title,
    String description,
    String initialPrompt,
    List<Question> questions) {
    
}
