package com.insyte.questionspark.backend.infrastructure.adapter.rest;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.print.attribute.standard.Media;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.insyte.questionspark.backend.application.mapper.StoryMapper;
import com.insyte.questionspark.backend.application.port.in.StoryManagementUseCase;
import com.insyte.questionspark.backend.domain.model.Story;
import com.insyte.questionspark.backend.infrastructure.adapter.rest.dto.StoryDto;
import com.insyte.questionspark.backend.infrastructure.adapter.rest.dto.StoryDetailDTO;

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
    
    private final StoryManagementUseCase storyManagementUseCase;

    public StoryController(StoryManagementUseCase storyManagementUseCase) {
        this.storyManagementUseCase = storyManagementUseCase;
    }

    @Operation(summary = "Get all stories", description = "Retrieves a list of all available stories")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all stories"),
        @ApiResponse(responseCode = "500", description = "Internal server error occurred")
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, 
                consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StoryDto>> getAllStories() throws Exception{
        List<StoryDto> stories = storyManagementUseCase.getAllStories()
            .stream()
            .map(StoryMapper::toDto)
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
        return ResponseEntity.ok(StoryMapper.toDetailDTO(story));
    }
    
}
