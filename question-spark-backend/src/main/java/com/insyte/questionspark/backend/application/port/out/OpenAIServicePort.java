package com.insyte.questionspark.backend.application.port.out;

import com.insyte.questionspark.backend.infrastructure.adapter.openai.dto.StoryGenerationResponse;

public interface OpenAIServicePort {
    StoryGenerationResponse generateStory(String initialPrompt) throws Exception;
}


