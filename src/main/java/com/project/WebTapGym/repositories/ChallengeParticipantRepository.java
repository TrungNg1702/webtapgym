package com.project.WebTapGym.repositories;

import com.project.WebTapGym.models.ChallengeParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChallengeParticipantRepository extends JpaRepository<ChallengeParticipant, Long> {
    boolean existsByChallengeIdAndUserId(Long challengeId, Long userId);
    List<ChallengeParticipant> findByChallengeId(Long challengeId);
    Optional<ChallengeParticipant> findByChallengeIdAndUserId(Long challengeId, Long userId);

}
