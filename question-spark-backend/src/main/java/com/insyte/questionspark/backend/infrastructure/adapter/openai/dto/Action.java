package com.insyte.questionspark.backend.infrastructure.adapter.openai.dto;

public record Action(
    String text,
    String followUpPrompt) {
    
}
