package com.insyte.questionspark.backend.infrastructure.adapter.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateStoryRequest (
    @NotBlank(message = "Initial prompt is required")
    String initialPrompt
) 
    
{}
