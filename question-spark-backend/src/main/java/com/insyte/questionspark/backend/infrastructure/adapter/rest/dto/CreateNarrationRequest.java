package com.insyte.questionspark.backend.infrastructure.adapter.rest.dto;

import java.util.UUID;
import com.fasterxml.jackson.databind.JsonNode;

public record CreateNarrationRequest (
    UUID parentNarrativeId,
    UUID questionId,
    String questionText,
    String response,
    String action,
    JsonNode nextNarrative,
    String profileId
) {}