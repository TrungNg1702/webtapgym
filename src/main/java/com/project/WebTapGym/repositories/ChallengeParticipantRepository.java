package com.project.WebTapGym.repositories;

import com.project.WebTapGym.models.ChallengeParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChallengeParticipantRepository extends JpaRepository<ChallengeParticipant, Long> {
    boolean existsByChallengeIdAndUserId(Long challengeId, Long userId);
    List<ChallengeParticipant> findByChallengeId(Long challengeId);
}
