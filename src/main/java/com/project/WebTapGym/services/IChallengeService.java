package com.project.WebTapGym.services;

import com.project.WebTapGym.models.Challenge;
import com.project.WebTapGym.models.ChallengeParticipant;

import java.time.LocalDate;
import java.util.List;

public interface IChallengeService {
    Challenge createChallenge(String title, String description, LocalDate startDate, LocalDate endDate);
    List<Challenge> getAllChallenges(); // Thêm
    Challenge getChallengeById(Long id); // Thêm
    Challenge updateChallenge(Long challengeId, String title, String description, LocalDate startDate, LocalDate endDate); // Thêm
    void deleteChallenge(Long id); // Thêm

    void joinChallenge(Long challengeId, Long userId);
    void leaveChallenge(Long challengeId, Long userId); // Thêm để bỏ tham gia
    List<ChallengeParticipant> getParticipantsByChallengeId(Long challengeId); // Thêm để lấy danh sách người tham gia
}
