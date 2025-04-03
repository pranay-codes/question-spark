package com.insyte.questionspark.backend.infrastructure.adapter.rest.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record StoryDto(UUID id, String title, String description, LocalDateTime createdAt) {
    
}
