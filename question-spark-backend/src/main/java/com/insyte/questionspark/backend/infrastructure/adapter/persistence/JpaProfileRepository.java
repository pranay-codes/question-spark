package com.insyte.questionspark.backend.infrastructure.adapter.persistence;

import com.insyte.questionspark.backend.application.port.out.ProfileRepositoryPort;
import com.insyte.questionspark.backend.domain.model.Profile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface JpaProfileRepository extends ProfileRepositoryPort, JpaRepository<Profile, UUID> {
}