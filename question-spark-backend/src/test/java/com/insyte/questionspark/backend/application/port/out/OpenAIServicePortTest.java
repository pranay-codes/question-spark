package com.insyte.questionspark.backend.application.port.out;

import com.insyte.questionspark.backend.infrastructure.adapter.openai.dto.Action;
import com.insyte.questionspark.backend.infrastructure.adapter.openai.dto.Question;
import com.insyte.questionspark.backend.infrastructure.adapter.openai.dto.StoryGenerationResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Arrays;

class OpenAIServicePortTest {

    @Mock
    private OpenAIServicePort openAIServicePort;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void generateStory_Success() throws Exception {
        // Arrange
        String prompt = "Tell me a story about a brave knight";
        StoryGenerationResponse expectedResponse = new StoryGenerationResponse(
                "The Brave Knight",
                "Story about a brave knight who saves the kingdom",
                "Once upon a time, in a faraway land, there was a brave knight who had a pet dragon.",
                Arrays.asList(
                        new Question(
                                "The knight's quest",
                                "What was the knight's quest?",
                                Arrays.asList(
                                        new Action("Save the kingdom", "What happened next?"),
                                        new Action("Find a treasure", "Where did they go?"),
                                        new Action("Rescue a princess", "Who was the princess?")
                                )
                        )
                )
        );
        when(openAIServicePort.generateStory(anyString())).thenReturn(expectedResponse);

        // Act
        StoryGenerationResponse actualResponse = openAIServicePort.generateStory(prompt);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void generateStory_ThrowsException() throws Exception {
        // Arrange
        String prompt = "Invalid prompt";
        when(openAIServicePort.generateStory(anyString())).thenThrow(new RuntimeException("API Error"));

        // Act & Assert
        assertThrows(Exception.class, () -> openAIServicePort.generateStory(prompt));
    }
}
