package com.insyte.questionspark.backend.infrastructure.adapter.openai.dto;

import java.util.List;

public record Question(
    String content,
    String questionText,
    List<Action> actions
) {
    
}
