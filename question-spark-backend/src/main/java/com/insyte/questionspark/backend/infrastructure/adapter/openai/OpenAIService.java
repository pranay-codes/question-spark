package com.insyte.questionspark.backend.infrastructure.adapter.openai;

import com.insyte.questionspark.backend.infrastructure.adapter.openai.dto.StoryGenerationResponse;

public interface OpenAIService {
    StoryGenerationResponse generateStory(String initialPrompt) throws Exception;
}


