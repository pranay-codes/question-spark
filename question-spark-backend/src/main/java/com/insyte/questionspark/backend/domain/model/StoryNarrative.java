package com.insyte.questionspark.backend.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "story_narrative")
public class StoryNarrative {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id")
    private Story story;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Profile user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_narrative_id")
    private StoryNarrative parentNarrative;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private StoryQuestion question;
    
    @Column(name = "choice_text", nullable = false)
    private String choiceText;
    
    @Column(name = "response_text", nullable = false)
    private String responseText;
    
    @Column(name = "next_narrative", columnDefinition = "jsonb")
    private String nextNarrative;
    
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

    public Story getStory() {
        return story;
    }

    public void setStory(Story story) {
        this.story = story;
    }

    public Profile getUser() {
        return user;
    }

    public void setUser(Profile user) {
        this.user = user;
    }

    public StoryNarrative getParentNarrative() {
        return parentNarrative;
    }

    public void setParentNarrative(StoryNarrative parentNarrative) {
        this.parentNarrative = parentNarrative;
    }

    public StoryQuestion getQuestion() {
        return question;
    }

    public void setQuestion(StoryQuestion question) {
        this.question = question;
    }

    public String getChoiceText() {
        return choiceText;
    }

    public void setChoiceText(String choiceText) {
        this.choiceText = choiceText;
    }

    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    public String getNextNarrative() {
        return nextNarrative;
    }

    public void setNextNarrative(String nextNarrative) {
        this.nextNarrative = nextNarrative;
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