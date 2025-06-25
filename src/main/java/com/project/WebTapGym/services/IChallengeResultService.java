package com.project.WebTapGym.services;

import com.project.WebTapGym.models.ChallengeResult;

public interface IChallengeResultService {
    void markCompleted(Long userId, Long challengeId);
    ChallengeResult getResult(Long userId, Long challengeId);
    void updateProgress(Long userId, Long challengeId, int progress);
}
