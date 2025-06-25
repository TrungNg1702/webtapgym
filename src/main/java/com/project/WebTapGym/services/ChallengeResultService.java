package com.project.WebTapGym.services;

import com.project.WebTapGym.models.Challenge;
import com.project.WebTapGym.models.ChallengeResult;
import com.project.WebTapGym.repositories.ChallengeRepository;
import com.project.WebTapGym.repositories.ChallengeResultRepository;
import com.project.WebTapGym.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChallengeResultService implements IChallengeResultService{

    private final ChallengeResultRepository challengeResultRepository;
    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;


    @Override
    public void markCompleted(Long userId, Long challengeId) {
        ChallengeResult result = challengeResultRepository.findByUserIdAndChallengeId(userId, challengeId)
                .orElseThrow(() -> new RuntimeException("Kết quả thử thách không tồn tại"));

        result.setCompleted(true);
        result.setCompletedAt(java.time.LocalDateTime.now());
        challengeResultRepository.save(result);
    }

    @Override
    public ChallengeResult getResult(Long userId, Long challengeId) {
        return challengeResultRepository.findByUserIdAndChallengeId(userId, challengeId)
                .orElseThrow(()-> new RuntimeException("Ket qua thu thach khong ton tai"));
    }

    @Override
    public void updateProgress(Long userId, Long challengeId, int progress) {
        ChallengeResult result = challengeResultRepository.findByUserIdAndChallengeId(userId, challengeId)
                .orElseThrow(() -> new RuntimeException("Kết quả thử thách không tồn tại"));
        result.setProgress(progress);
        if(progress == 100){
            result.setCompleted(true);
            result.setCompletedAt(java.time.LocalDateTime.now());
        }
        challengeResultRepository.save(result);
    }
}
