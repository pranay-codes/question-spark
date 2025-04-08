package com.insyte.questionspark.backend.infrastructure.adapter.rest.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record StoryNarrativeDTO(
    UUID id,
    UUID storyId,
    String content,
    LocalDateTime createdAt
) {}
