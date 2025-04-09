package com.insyte.questionspark.backend.infrastructure.adapter.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.containsString;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.insyte.questionspark.backend.application.mapper.StoryMapper;
import com.insyte.questionspark.backend.application.port.in.StoryManagementUseCase;
import com.insyte.questionspark.backend.application.port.in.StoryNarrationUseCase;
import com.insyte.questionspark.backend.domain.exception.ServiceException;
import com.insyte.questionspark.backend.domain.exception.StoryNotFoundException;
import com.insyte.questionspark.backend.domain.model.Story;
import com.insyte.questionspark.backend.domain.model.StoryNarrative;
import com.insyte.questionspark.backend.domain.model.StoryQuestion;
import com.insyte.questionspark.backend.infrastructure.adapter.rest.dto.CreateNarrationRequest;
import com.insyte.questionspark.backend.infrastructure.adapter.rest.dto.StoryDTO;
import com.insyte.questionspark.backend.infrastructure.adapter.rest.dto.StoryDetailDTO;
import com.insyte.questionspark.backend.infrastructure.adapter.rest.dto.StoryNarrativeDTO;
import com.insyte.questionspark.backend.infrastructure.adapter.rest.exception.GlobalExceptionHandler;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class StoryControllerTest {
    
    @Mock
    private StoryManagementUseCase storyManagementUseCase;

    @Mock
    private StoryMapper storyMapper;

    @Mock
    private StoryNarrationUseCase storyNarrationUseCase;

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
        when(storyMapper.toDto(story1)).thenReturn(new StoryDTO(story1.getId(), story1.getTitle(), story1.getDescription(), story1.getCreatedAt()));
        when(storyMapper.toDto(story2)).thenReturn(new StoryDTO(story2.getId(), story2.getTitle(), story2.getDescription(), story2.getCreatedAt()));

        // Act & Assert
        mockMvc.perform(get("/api/v1/stories")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("Story 1")))
                .andExpect(jsonPath("$[1].title", is("Story 2")));

        verify(storyManagementUseCase).getAllStories();

        verify(storyMapper).toDto(story1);
        verify(storyMapper).toDto(story2);
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
    void createNarration_ReturnsCreatedNarration_WhenSuccessful() throws Exception {
        // Arrange
        UUID storyId = UUID.randomUUID();
        CreateNarrationRequest request = new CreateNarrationRequest(
            null, 
            UUID.randomUUID(), 
            "questionText", 
            "responseText", 
            "action", 
            null, 
            UUID.randomUUID().toString());
        StoryNarrative narrative = new StoryNarrative();
        narrative.setId(UUID.randomUUID());
        narrative.setChoiceText("choiceText");
        narrative.setResponseText("responseText");
        narrative.setCreatedAt(LocalDateTime.now());

        when(storyNarrationUseCase.createNarration(any(), any()))
            .thenReturn(narrative);
        
            System.out.println("STORY NARRATIVE--->" + narrative.getId());
        when(storyMapper.toNarrativeDTO(any(StoryNarrative.class))).thenReturn(new StoryNarrativeDTO(
            narrative.getId(),
            storyId,
            narrative.getResponseText(),
            narrative.getCreatedAt()
        ));

        
        // Act & Assert
        mockMvc.perform(post("/api/v1/stories/{storyId}/narration", storyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(narrative.getId().toString())))

                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.storyId", is(storyId.toString())))
                .andExpect(jsonPath("$.content", is("responseText")))
                .andExpect(jsonPath("$.createdAt").exists());


// System.out.println("CONTENT TYPE--->" + result.getResponse().getContentType());


    verify(storyNarrationUseCase).createNarration(eq(storyId), argThat(req ->
        Objects.equals(req.action(), request.action()) &&
        Objects.equals(req.questionId(), request.questionId()) 
    )); // Verify that the request object passed to the use case matches the expected values       
    
    verify(storyMapper).toNarrativeDTO(narrative);
    }

    @Test
    void createNarration_ReturnsBadRequest_WhenRequestBodyIsInvalid() throws Exception {
        UUID storyId = UUID.randomUUID();
        String invalidJson = "{\"invalid\": \"json\"}";

        mockMvc.perform(post("/api/v1/stories/{storyId}/narration", storyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.code", is("400")));
    }

    @Test
    void createNarration_ReturnsNotFound_WhenStoryDoesNotExist() throws Exception {
        UUID storyId = UUID.randomUUID();
        CreateNarrationRequest request = new CreateNarrationRequest(
            null, UUID.randomUUID(), "questionText", "responseText", 
            "action", null, UUID.randomUUID().toString());

        when(storyNarrationUseCase.createNarration(any(), any()))
            .thenThrow(new StoryNotFoundException("Story not found"));

        mockMvc.perform(post("/api/v1/stories/{storyId}/narration", storyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Story not found")))
                .andExpect(jsonPath("$.code", is("404")));
    }

    @Test
    void createNarration_ReturnsServerError_WhenUnexpectedErrorOccurs() throws Exception {
        UUID storyId = UUID.randomUUID();
        CreateNarrationRequest request = new CreateNarrationRequest(
            null, UUID.randomUUID(), "questionText", "responseText", 
            "action", null, UUID.randomUUID().toString());

        when(storyNarrationUseCase.createNarration(any(), any()))
            .thenThrow(new ServiceException("Unexpected error"));

        mockMvc.perform(post("/api/v1/stories/{storyId}/narration", storyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message", is("Unexpected error")))
                .andExpect(jsonPath("$.code", is("001")));
    }

    @Test
    void createNarration_ReturnsBadRequest_WhenInvalidUUID() throws Exception {
        CreateNarrationRequest request = new CreateNarrationRequest(
            null, UUID.randomUUID(), "questionText", "responseText", 
            "action", null, UUID.randomUUID().toString());

        mockMvc.perform(post("/api/v1/stories/invalid-uuid/narration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Invalid UUID")))
                .andExpect(jsonPath("$.code", is("400")));
    }

    @Test
    void createNarration_ReturnsBadRequest_WhenQuestionIdIsMissing() throws Exception {
        // Arrange
        UUID storyId = UUID.randomUUID();
        String requestWithMissingQuestionId = """
            {
                "responseText": "Some response",
                "action": "Some action",
                "questionText": "Some question",
                "narrativeId": "123e4567-e89b-12d3-a456-426614174000"
            }
            """;

        // Act & Assert
        mockMvc.perform(post("/api/v1/stories/{storyId}/narration", storyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestWithMissingQuestionId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("questionId")))
                .andExpect(jsonPath("$.code", is("400")));
    }

    @Test
    void createNarration_ReturnsBadRequest_WhenEmptyResponseText() throws Exception {
        // Arrange
        UUID storyId = UUID.randomUUID();
        String requestWithEmptyResponse = """
            {
                "questionId": "123e4567-e89b-12d3-a456-426614174000",
                "response": "",
                "action": "Some action",
                "questionText": "Some question",
                "narrativeId": "123e4567-e89b-12d3-a456-426614174000",
                "nextNarrative": "nextNarrative"

            }
            """;

        // Act & Assert
        mockMvc.perform(post("/api/v1/stories/{storyId}/narration", storyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestWithEmptyResponse))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("response")))
                .andExpect(jsonPath("$.code", is("400")));
    }

    @Test
    void createNarration_HandlesLargeContent() throws Exception {
        UUID storyId = UUID.randomUUID();
        String largeText = "a".repeat(10000); // Create a large response text
        CreateNarrationRequest request = new CreateNarrationRequest(
            null, UUID.randomUUID(), "questionText", largeText, 
            "action", null, UUID.randomUUID().toString());

        StoryNarrative narrative = new StoryNarrative();
        narrative.setId(UUID.randomUUID());
        narrative.setResponseText(largeText);
        narrative.setCreatedAt(LocalDateTime.now());

        when(storyNarrationUseCase.createNarration(any(), any()))
            .thenReturn(narrative);
        when(storyMapper.toNarrativeDTO(any())).thenReturn(new StoryNarrativeDTO(
            narrative.getId(), storyId, narrative.getResponseText(), narrative.getCreatedAt()
        ));

        mockMvc.perform(post("/api/v1/stories/{storyId}/narration", storyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", is(largeText)));
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
        when(storyMapper.toDto(story)).thenReturn(new StoryDTO(story.getId(), story.getTitle(), story.getDescription(), story.getCreatedAt()));

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
        when(storyMapper.toDetailDTO(story))
            .thenReturn(
                new StoryDetailDTO(
                    storyId, 
                    story.getTitle(), 
                    story.getDescription(), 
                    story.getInitialPrompt(), 
                    story.getQuestions().get(0).getId(), 
                    story.getQuestions().get(0).getQuestionText()));

        // Act & Assert
        mockMvc.perform(get("/api/v1/stories/{id}", storyId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(storyId.toString())))
                .andExpect(jsonPath("$.title", is("Test Story")))
                .andExpect(jsonPath("$.description", is("Description")))
                .andExpect(jsonPath("$.initialPrompt", is("Initial prompt")))
                .andExpect(jsonPath("$.question").exists());

        verify(storyManagementUseCase).getStoryWithQuestions(storyId);
        verify(storyMapper).toDetailDTO(story);
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

        when(storyMapper.toDetailDTO(story))
            .thenReturn(
                new StoryDetailDTO(
                    storyId, 
                    story.getTitle(), 
                    story.getDescription(), 
                    story.getInitialPrompt(), 
                    story.getQuestions().get(0).getId(), 
                    story.getQuestions().get(0).getQuestionText()));

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

    private StoryQuestion createSampleQuestion() throws JsonMappingException, JsonProcessingException {
        StoryQuestion question = new StoryQuestion();
        question.setId(UUID.randomUUID());
        ObjectMapper objectMapper = new ObjectMapper();
        question.setQuestionText(objectMapper.readTree("""
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
        """));
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
