package com.project.WebTapGym.services;

import com.project.WebTapGym.models.Challenge;

import java.time.LocalDate;

public interface IChallengeService {
    Challenge createChallenge(String title, String description, LocalDate startDate, LocalDate endDate);
    void joinChallenge(Long challengeId, Long userId);
}
