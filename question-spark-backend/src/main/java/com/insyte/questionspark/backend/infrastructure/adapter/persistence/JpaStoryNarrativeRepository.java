package com.insyte.questionspark.backend.infrastructure.adapter.persistence;

import com.insyte.questionspark.backend.application.port.out.StoryNarrativeRepositoryPort;
import com.insyte.questionspark.backend.domain.model.StoryNarrative;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface JpaStoryNarrativeRepository extends StoryNarrativeRepositoryPort, JpaRepository<StoryNarrative, UUID> {
}