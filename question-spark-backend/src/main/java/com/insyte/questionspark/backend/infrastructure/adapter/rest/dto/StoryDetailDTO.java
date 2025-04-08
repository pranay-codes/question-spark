package com.insyte.questionspark.backend.infrastructure.adapter.rest.dto;

import java.util.UUID;
import com.fasterxml.jackson.databind.JsonNode;

public record StoryDetailDTO(
    UUID id,
    String title,
    String description,
    String initialPrompt,
    UUID questionId,
    JsonNode question
) {}
