package com.insyte.questionspark.backend.infrastructure.adapter.openai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.insyte.questionspark.backend.application.port.out.OpenAIServicePort;
import com.insyte.questionspark.backend.infrastructure.adapter.openai.dto.StoryGenerationResponse;
import com.insyte.questionspark.backend.infrastructure.config.OpenAIProperties;

import java.util.List;

import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

@Service
public class OpenAIServiceImpl implements OpenAIServicePort {
    private final ChatModel chatModel;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OpenAIProperties openAIProperties;

    public OpenAIServiceImpl( ChatModel chatModel, OpenAIProperties openAIProperties) {
        this.chatModel = chatModel;
        this.openAIProperties = openAIProperties;
    }

    @Override
    public StoryGenerationResponse generateStory(String initialPrompt) throws Exception {
        String systemPrompt = """
            Generate a story with the following format:
            1. A title
            2. A description
            3. An initial prompt
            4. Three questions, each containing:
               - Content the question relates to
               - Question text
               - Three possible actions, each with:
                 * Action text
                 * Follow-up prompt
            Return as JSON.
            """;

        ChatResponse response = chatModel.call(new Prompt(
            List.of(
                new SystemMessage(systemPrompt), 
                new UserMessage(initialPrompt)), 
            OpenAiChatOptions.builder()
                .model(openAIProperties.getModel())
                .maxTokens(this.openAIProperties.getMaxTokens())
                .temperature(this.openAIProperties.getTemperature())
                .build()
            ));





        String content = response.getResult().getOutput().getText();
        
        if (content == null || content.isEmpty()) {
            throw new OpenAIException("Failed to generate story");
        }

        return objectMapper.readValue(content, StoryGenerationResponse.class);
    }
}
