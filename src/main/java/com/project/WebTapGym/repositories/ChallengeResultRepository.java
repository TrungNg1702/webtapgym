package com.project.WebTapGym.repositories;

import com.project.WebTapGym.models.ChallengeResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChallengeResultRepository extends JpaRepository<ChallengeResult, Long> {
    Optional<ChallengeResult> findByUserIdAndChallengeId(Long userId, Long challengeId);
}
