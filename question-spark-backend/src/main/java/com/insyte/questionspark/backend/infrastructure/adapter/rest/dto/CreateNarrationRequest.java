package com.insyte.questionspark.backend.infrastructure.adapter.rest.dto;

import java.util.UUID;
import com.fasterxml.jackson.databind.JsonNode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateNarrationRequest (
    UUID parentNarrativeId,
    @NotNull UUID questionId,
    @NotNull @NotBlank String questionText,
    @NotNull @NotBlank String response,
    @NotNull @NotBlank String action,
    @NotNull JsonNode nextNarrative,
    String profileId
) {}