package com.insyte.questionspark.backend.infrastructure.adapter.rest;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.print.attribute.standard.Media;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.insyte.questionspark.backend.application.mapper.StoryMapper;
import com.insyte.questionspark.backend.application.port.in.StoryManagementUseCase;
import com.insyte.questionspark.backend.application.port.in.StoryNarrationUseCase;
import com.insyte.questionspark.backend.domain.model.Story;
import com.insyte.questionspark.backend.domain.model.StoryNarrative;
import com.insyte.questionspark.backend.infrastructure.adapter.rest.dto.StoryDTO;
import com.insyte.questionspark.backend.infrastructure.adapter.rest.dto.StoryDetailDTO;
import com.insyte.questionspark.backend.infrastructure.adapter.rest.dto.CreateNarrationRequest;
import com.insyte.questionspark.backend.infrastructure.adapter.rest.dto.StoryNarrativeDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/api/v1/stories")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Story", description = "Story management APIs")
public class StoryController {
    
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(StoryController.class);

    private final StoryManagementUseCase storyManagementUseCase;
    private final StoryNarrationUseCase storyNarrationUseCase;
    private final StoryMapper storyMapper;

    public StoryController(
        StoryManagementUseCase storyManagementUseCase,
        StoryNarrationUseCase storyNarrationUseCase,
        StoryMapper storyMapper
    ) {
        this.storyManagementUseCase = storyManagementUseCase;
        this.storyNarrationUseCase = storyNarrationUseCase;
        this.storyMapper = storyMapper;
    }

    @Operation(summary = "Get all stories", description = "Retrieves a list of all available stories")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all stories"),
        @ApiResponse(responseCode = "500", description = "Internal server error occurred")
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, 
                consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StoryDTO>> getAllStories() throws Exception{
        List<StoryDTO> stories = storyManagementUseCase.getAllStories()
            .stream()
            .map(storyMapper::toDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(stories);
    }

    @Operation(summary = "Get story by ID", description = "Retrieves a specific story with its questions by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the story"),
        @ApiResponse(responseCode = "404", description = "Story not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error occurred")
    })
    @GetMapping(value = "/{storyId}", 
                produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StoryDetailDTO> getStoryById(
            @Parameter(description = "ID of the story to retrieve") 
            @PathVariable("storyId") UUID storyId) throws Exception {
        Story story = storyManagementUseCase.getStoryWithQuestions(storyId);
        return ResponseEntity.ok(storyMapper.toDetailDTO(story));
    }

    @Operation(summary = "Create story narration", description = "Creates a new narration for a story")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Successfully created narration"),
        @ApiResponse(responseCode = "404", description = "Story not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error occurred")
    })
    @PostMapping(
        value = "/{storyId}/narration",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<StoryNarrativeDTO> createNarration(
        @Parameter(description = "ID of the story") 
        @PathVariable("storyId") UUID storyId,
        @Parameter(description = "Narration details") 
        @RequestBody CreateNarrationRequest request
    ) throws Exception {
        try {
            StoryNarrative narrative = storyNarrationUseCase.createNarration(storyId, request);
            StoryNarrativeDTO narrativeDTO = storyMapper.toNarrativeDTO(narrative);
            return ResponseEntity.status(HttpStatus.OK).body(narrativeDTO);
        } catch (Exception e) {
            LOGGER.error("Error creating narration for story ID {}: {}", storyId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Or a more specific error response
        }
    }

}
