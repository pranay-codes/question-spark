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
import com.insyte.questionspark.backend.domain.exception.StoryNotFoundException;
import com.insyte.questionspark.backend.domain.model.Story;
import com.insyte.questionspark.backend.domain.model.StoryQuestion;
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

    // GET /api/v1/stories tests
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

    // GET /api/v1/stories/{id} tests
    @Test
    void getStoryById_ReturnsStory_WhenStoryExists() throws Exception {
        // Arrange
        UUID storyId = UUID.randomUUID();
        Story story = createStoryWithQuestions(storyId, "Test Story", 
            "Description", "Initial prompt", 
            Collections.singletonList(createSampleQuestion()));
        
        when(storyManagementUseCase.getStoryWithQuestions(storyId)).thenReturn(story);

        // Act & Assert
        mockMvc.perform(get("/api/v1/stories/{id}", storyId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(storyId.toString())))
                .andExpect(jsonPath("$.title", is("Test Story")))
                .andExpect(jsonPath("$.description", is("Description")))
                .andExpect(jsonPath("$.initialPrompt", is("Initial prompt")))
                .andExpect(jsonPath("$.questions").exists());

        verify(storyManagementUseCase).getStoryWithQuestions(storyId);
    }

    @Test
    void getStoryById_Returns404_WhenStoryNotFound() throws Exception {
        // Arrange
        UUID storyId = UUID.randomUUID();
        when(storyManagementUseCase.getStoryWithQuestions(storyId))
            .thenThrow(new StoryNotFoundException("Story not found"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/stories/{id}", storyId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Story not found")))
                .andExpect(jsonPath("$.code", is("404")));

        verify(storyManagementUseCase).getStoryWithQuestions(storyId);
    }

    @Test
    void getStoryById_Returns400_WhenInvalidUUID() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/stories/invalid-uuid")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Invalid UUID")))
                .andExpect(jsonPath("$.code", is("400")));
    }

    @Test
    void getStoryById_HandlesSpecialCharacters_InStoryData() throws Exception {
        // Arrange
        UUID storyId = UUID.randomUUID();
        Story story = createStoryWithQuestions(storyId, 
            "Story with special chars: !@#$%^&*()", 
            "Description with üñíçødé", 
            "Initial prompt with 中文",
            Collections.singletonList(createSampleQuestion()));
        
        when(storyManagementUseCase.getStoryWithQuestions(storyId)).thenReturn(story);

        // Act & Assert
        mockMvc.perform(get("/api/v1/stories/{id}", storyId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Story with special chars: !@#$%^&*()")))
                .andExpect(jsonPath("$.description", is("Description with üñíçødé")))
                .andExpect(jsonPath("$.initialPrompt", is("Initial prompt with 中文")));
    }

    // Helper Methods
    private Story createStoryWithQuestions(UUID id, String title, 
        String description, String initialPrompt, List<StoryQuestion> questions) {
        Story story = new Story();
        story.setId(id);
        story.setTitle(title);
        story.setDescription(description);
        story.setInitialPrompt(initialPrompt);
        story.setQuestions(questions);
        return story;
    }

    private StoryQuestion createSampleQuestion() {
        StoryQuestion question = new StoryQuestion();
        question.setId(UUID.randomUUID());
        question.setQuestionText("""
        {
          "question": {
            "type": "text",
            "content": "What should the robot do after finding the puppy?"
          },
          "response": {
            "type": "generated",
            "content": "The robot gently picks up the puppy and looks around."
          },
          "follow_ups": [
            {
              "action": "Search for the puppy's owner",
              "narrative": {
                "id": "narrative-001",
                "content": "The robot walks around the park."
              }
            }
          ]
        }
        """);
        return question;
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
