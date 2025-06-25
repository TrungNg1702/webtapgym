package com.project.WebTapGym.services;

import com.project.WebTapGym.models.Challenge;
import com.project.WebTapGym.models.ChallengeParticipant;
import com.project.WebTapGym.models.User;
import com.project.WebTapGym.repositories.ChallengeParticipantRepository;
import com.project.WebTapGym.repositories.ChallengeRepository;
import com.project.WebTapGym.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ChallengeService implements IChallengeService {
    private final ChallengeRepository challengeRepository;
    private final UserRepository userRepository;
    private final ChallengeParticipantRepository participantRepository;

    @Override
    public Challenge createChallenge(String title, String description, LocalDate startDate, LocalDate endDate) {
        Challenge challenge = Challenge.builder()
                .title(title)
                .description(description)
                .startDate(startDate)
                .endDate(endDate)
                .build();
        return challengeRepository.save(challenge);
    }

    @Override
    public void joinChallenge(Long challengeId, Long userId) {
        if (participantRepository.existsByChallengeIdAndUserId(challengeId, userId)) {
            throw new RuntimeException("User đã tham gia thử thách rồi");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User không tồn tại"));
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(() -> new RuntimeException("Challenge không tồn tại"));
        ChallengeParticipant participant = ChallengeParticipant.builder()
                .user(user)
                .challenge(challenge)
                .build();
        participantRepository.save(participant);
    }
}

