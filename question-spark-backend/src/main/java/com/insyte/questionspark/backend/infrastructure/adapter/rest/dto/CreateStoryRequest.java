package com.insyte.questionspark.backend.infrastructure.adapter.rest.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateStoryRequest {
    @NotBlank(message = "Initial prompt is required")
    private String initialPrompt;

    public String getInitialPrompt() {
        return initialPrompt;
    }

    public void setInitialPrompt(String initialPrompt) {
        this.initialPrompt = initialPrompt;
    }
}
