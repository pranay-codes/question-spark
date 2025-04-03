package com.insyte.questionspark.backend.infrastructure.adapter.rest;

import java.util.List;
import java.util.stream.Collectors;

import javax.print.attribute.standard.Media;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.insyte.questionspark.backend.application.mapper.StoryMapper;
import com.insyte.questionspark.backend.application.port.in.StoryManagementUseCase;
import com.insyte.questionspark.backend.infrastructure.adapter.rest.dto.StoryDto;

@RestController
@RequestMapping("/api/v1/stories")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class StoryController {
    
    private final StoryManagementUseCase storyManagementUseCase;

    public StoryController(StoryManagementUseCase storyManagementUseCase) {
        this.storyManagementUseCase = storyManagementUseCase;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StoryDto>> getAllStories() throws Exception{
        List<StoryDto> stories = storyManagementUseCase.getAllStories()
            .stream()
            .map(StoryMapper::toDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(stories);
    }
    
}
