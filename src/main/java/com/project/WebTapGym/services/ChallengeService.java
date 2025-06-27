package com.project.WebTapGym.services;

import com.project.WebTapGym.models.Challenge;
import com.project.WebTapGym.models.ChallengeParticipant;
import com.project.WebTapGym.models.User;
import com.project.WebTapGym.repositories.ChallengeParticipantRepository;
import com.project.WebTapGym.repositories.ChallengeRepository;
import com.project.WebTapGym.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
                .build();

        if(!startDate.isBefore(LocalDate.now()) && !startDate.isAfter(endDate)){
            challenge.setStartDate(startDate);
            challenge.setEndDate(endDate);
        }else {
            throw new RuntimeException("Ngày bắt đầu không được trước ngày hiện tại hoặc không được sau ngày kết thúc");
        }
        return challengeRepository.save(challenge);
    }

    @Override
    public List<Challenge> getAllChallenges() {
        return challengeRepository.findAll();
    }

    @Override
    public Challenge getChallengeById(Long id) {
        return challengeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Challenge not found")) ;
    }

    @Override
    @Transactional
    public Challenge updateChallenge(Long challengeId, String title, String description, LocalDate startDate, LocalDate endDate) {
        Challenge existingChallenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thử thách để cập nhật."));

        existingChallenge.setTitle(title);
        existingChallenge.setDescription(description);

        if(!startDate.isBefore(LocalDate.now()) && !startDate.isAfter(endDate)){
            existingChallenge.setStartDate(startDate);
            existingChallenge.setEndDate(endDate);
        }else {
            throw new RuntimeException("Ngày bắt đầu không được trước ngày hiện tại hoặc không được sau ngày kết thúc");
        }

        existingChallenge.setUpdatedAt(LocalDateTime.now());

        return challengeRepository.save(existingChallenge);
    }

    @Override
    @Transactional
    public void deleteChallenge(Long id) {
        if (!challengeRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy thử thách để xóa.");
        }
        challengeRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void joinChallenge(Long challengeId, Long userId) {
        // Kiểm tra xem đã tham gia chưa
        if (participantRepository.existsByChallengeIdAndUserId(challengeId, userId)) {
            throw new RuntimeException("Người dùng đã tham gia thử thách này rồi.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng."));
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thử thách."));

        ChallengeParticipant participant = ChallengeParticipant.builder()
                .user(user)
                .challenge(challenge)
                .build();
        participantRepository.save(participant);
    }

    @Override
    @Transactional
    public void leaveChallenge(Long challengeId, Long userId) {
        ChallengeParticipant participant = participantRepository.findByChallengeIdAndUserId(challengeId, userId)
                .orElseThrow(() -> new RuntimeException("Người dùng chưa tham gia thử thách này để có thể rời đi."));
        participantRepository.delete(participant);
    }

    @Override
    public List<ChallengeParticipant> getParticipantsByChallengeId(Long challengeId) {
        return participantRepository.findByChallengeId(challengeId) ;
    }
}

