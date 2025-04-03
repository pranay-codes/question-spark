package com.insyte.questionspark.backend.infrastructure.adapter.persistence;

import com.insyte.questionspark.backend.application.port.out.StoryQuestionRepositoryPort;
import com.insyte.questionspark.backend.domain.model.StoryQuestion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface JpaStoryQuestionRepository extends StoryQuestionRepositoryPort, JpaRepository<StoryQuestion, UUID> {
}