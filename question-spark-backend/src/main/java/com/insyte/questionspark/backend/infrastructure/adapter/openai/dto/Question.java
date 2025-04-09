package com.insyte.questionspark.backend.infrastructure.adapter.openai.dto;

public record Question(
    String content,
    String questionText,
    Action[] actions
) {
    
}
