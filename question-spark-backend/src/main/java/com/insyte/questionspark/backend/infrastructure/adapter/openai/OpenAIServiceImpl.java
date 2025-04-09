package com.insyte.questionspark.backend.infrastructure.adapter.openai;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.insyte.questionspark.backend.infrastructure.adapter.openai.dto.StoryGenerationResponse;
import com.insyte.questionspark.backend.infrastructure.config.OpenAIProperties;

@Service
public class OpenAIServiceImpl implements OpenAIService {
    private final RestTemplate restTemplate;
    private final OpenAIProperties properties;
    private final ObjectMapper objectMapper;

    public OpenAIServiceImpl(OpenAIProperties properties) {
        this.properties = properties;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
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

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(properties.getApiKey());

        var request = Map.of(
            "model", properties.getModel(),
            "messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", initialPrompt)
            ),
            "temperature", 0.7
        );

        var response = restTemplate.postForObject(
            properties.getEndpoint(),
            new HttpEntity<>(request, headers),
            OpenAIResponse.class
        );

        if (response == null || response.choices().isEmpty()) {
            throw new OpenAIException("Failed to generate story");
        }

        String content = response.choices().get(0).message().content();
        return objectMapper.readValue(content, StoryGenerationResponse.class);
    }
}

record OpenAIResponse(List<Choice> choices) {}
record Choice(Message message) {}
record Message(String content) {}
