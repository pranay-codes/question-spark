package com.insyte.questionspark.backend.infrastructure.adapter.openai.dto;

import org.aspectj.weaver.patterns.TypePatternQuestions.Question;

public record StoryGenerationResponse(
    String title,
    String description,
    String initialPrompt,
    Question[] questions) {
    
}
