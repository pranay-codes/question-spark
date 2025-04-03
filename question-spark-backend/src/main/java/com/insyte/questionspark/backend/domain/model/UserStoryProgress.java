package com.insyte.questionspark.backend.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_story_progress")
public class UserStoryProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Profile user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id")
    private Story story;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_narrative_id")
    private StoryNarrative currentNarrative;
    
    @Column(name = "progress_path", columnDefinition = "jsonb", nullable = false)
    private String progressPath;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Profile getUser() {
        return user;
    }

    public void setUser(Profile user) {
        this.user = user;
    }

    public Story getStory() {
        return story;
    }

    public void setStory(Story story) {
        this.story = story;
    }

    public StoryNarrative getCurrentNarrative() {
        return currentNarrative;
    }

    public void setCurrentNarrative(StoryNarrative currentNarrative) {
        this.currentNarrative = currentNarrative;
    }

    public String getProgressPath() {
        return progressPath;
    }

    public void setProgressPath(String progressPath) {
        this.progressPath = progressPath;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}