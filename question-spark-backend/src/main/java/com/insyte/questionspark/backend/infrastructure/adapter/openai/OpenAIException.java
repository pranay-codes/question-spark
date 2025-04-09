package com.insyte.questionspark.backend.infrastructure.adapter.openai;

public class OpenAIException extends RuntimeException {
    public OpenAIException(String message) {
        super(message);
    }

    public OpenAIException(String message, Throwable cause) {
        super(message, cause);
    }
}
