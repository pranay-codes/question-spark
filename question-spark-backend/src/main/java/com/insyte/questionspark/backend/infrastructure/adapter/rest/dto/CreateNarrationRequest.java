package com.insyte.questionspark.backend.infrastructure.adapter.rest.dto;

import java.util.UUID;
import com.fasterxml.jackson.databind.JsonNode;

import jakarta.validation.constraints.NotNull;

public record CreateNarrationRequest (
    UUID parentNarrativeId,
    @NotNull UUID questionId,
    @NotNull String questionText,
    @NotNull String response,
    @NotNull String action,
    @NotNull JsonNode nextNarrative,
    String profileId
) {}