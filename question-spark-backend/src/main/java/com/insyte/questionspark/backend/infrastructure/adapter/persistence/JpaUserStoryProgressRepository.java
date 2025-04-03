package com.insyte.questionspark.backend.infrastructure.adapter.persistence;

import com.insyte.questionspark.backend.application.port.out.UserStoryProgressRepositoryPort;
import com.insyte.questionspark.backend.domain.model.UserStoryProgress;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface JpaUserStoryProgressRepository extends UserStoryProgressRepositoryPort, JpaRepository<UserStoryProgress, UUID> {
}