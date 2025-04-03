package com.insyte.questionspark.backend.infrastructure.adapter.persistence;

import com.insyte.questionspark.backend.application.port.out.StoryRepositoryPort;
import com.insyte.questionspark.backend.domain.model.Story;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface JpaStoryRepository extends StoryRepositoryPort, JpaRepository<Story, UUID> {
}