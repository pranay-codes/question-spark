package com.insyte.questionspark.backend.application.port.out;

import com.insyte.questionspark.backend.domain.model.Profile;
import java.util.UUID;
import java.util.Optional;

public interface ProfileRepositoryPort {
    Profile save(Profile profile);
    Optional<Profile> findById(UUID id);
}