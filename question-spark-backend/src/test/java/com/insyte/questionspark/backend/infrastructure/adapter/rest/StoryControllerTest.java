package com.insyte.questionspark.backend.infrastructure.adapter.rest;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.containsString;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.insyte.questionspark.backend.application.port.in.StoryManagementUseCase;
import com.insyte.questionspark.backend.domain.exception.ServiceException;
import com.insyte.questionspark.backend.domain.model.Story;
import com.insyte.questionspark.backend.infrastructure.adapter.rest.exception.GlobalExceptionHandler;

@ExtendWith(MockitoExtension.class)
public class StoryControllerTest {
    
    @Mock
    private StoryManagementUseCase storyManagementUseCase;

    @InjectMocks
    private StoryController storyController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(storyController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    }

    @Test
    void test_GetAllStories_ReturnsStories_WhenStoriesExist() throws Exception {
        // Arrange
        Story story1 = createSampleStory("Story 1");
        Story story2 = createSampleStory("Story 2");
        List<Story> stories = Arrays.asList(story1, story2);
        
        when(storyManagementUseCase.getAllStories()).thenReturn(stories);

        // Act & Assert
        mockMvc.perform(get("/api/v1/stories")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("Story 1")))
                .andExpect(jsonPath("$[1].title", is("Story 2")));

        verify(storyManagementUseCase).getAllStories();
    }

    @Test
    void testGetAllStories_ReturnsEmptyList_WhenNoStoriesExist() throws Exception {
        // Arrange
        when(storyManagementUseCase.getAllStories()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/v1/stories")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(storyManagementUseCase).getAllStories();
    }

    @Test
    void getAllStories_HandlesException_WhenServiceThrowsError() throws Exception {
        // Arrange
        when(storyManagementUseCase.getAllStories())
            .thenThrow(new ServiceException("Service error"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/stories")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message", is("Service error")))
                .andExpect(jsonPath("$.code", is("001")));

        verify(storyManagementUseCase).getAllStories();
    }

    @Test
    void getAllStories_ReturnsBadRequest_WhenInvalidContentType() throws Exception {
        mockMvc.perform(get("/api/v1/stories")
                .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Unsupported media type: Content-Type 'text/plain' is not supported")))
                .andExpect(jsonPath("$.code", is("415")));
    }

    @Test
    void getAllStories_Returns404_WhenUrlIsMalformed() throws Exception {
        mockMvc.perform(get("/api/v1/storie")  // Intentionally malformed URL
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(containsString("Resource not found")))
                .andExpect(jsonPath("$.code").value("404"));
    }

    @Test
    void getAllStories_HandlesSpecialCharacters_Successfully() throws Exception {
        // Arrange
        Story story = createSampleStory("Story with special chars: !@#$%^&*()");
        when(storyManagementUseCase.getAllStories()).thenReturn(List.of(story));

        // Act & Assert
        mockMvc.perform(get("/api/v1/stories")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", is("Story with special chars: !@#$%^&*()")));
    }

    private Story createSampleStory(String title) {
        Story story = new Story();
        story.setId(UUID.randomUUID());
        story.setTitle(title);
        story.setDescription("Sample description");
        story.setInitialPrompt("Sample prompt");
        return story;
    }

}
