package com.project.WebTapGym.controllers;

import com.project.WebTapGym.models.Challenge;
import com.project.WebTapGym.models.ChallengeParticipant;
import com.project.WebTapGym.responses.ChallengeParticipantResponse;
import com.project.WebTapGym.responses.ChallengeResponse;
import com.project.WebTapGym.services.IChallengeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/challenges")
public class ChallengeController {

    private final IChallengeService challengeService;

    // challenges
    @PostMapping("")
    public ResponseEntity<?> createChallenge(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate) {
        try {
            Challenge challenge = challengeService.createChallenge(title, description, startDate, endDate);
            return ResponseEntity.status(HttpStatus.CREATED).body(ChallengeResponse.fromChallenge(challenge));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // challenges
    @GetMapping("")
    public ResponseEntity<List<ChallengeResponse>> getAllChallenges() {
        List<Challenge> challenges = challengeService.getAllChallenges();
        List<ChallengeResponse> challengeResponses = challenges.stream()
                .map(ChallengeResponse::fromChallenge)
                .collect(Collectors.toList());
        return ResponseEntity.ok(challengeResponses);
    }

    // challenges/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ChallengeResponse> getChallengeById(@PathVariable Long id) {
        try {
            Challenge challenge = challengeService.getChallengeById(id);
            return ResponseEntity.ok(ChallengeResponse.fromChallenge(challenge));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // challenges/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> updateChallenge(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate) {
        try {
            Challenge updatedChallenge = challengeService.updateChallenge(id, title, description, startDate, endDate);
            return ResponseEntity.ok(ChallengeResponse.fromChallenge(updatedChallenge));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // challenges/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteChallenge(@PathVariable Long id) {
        try {
            challengeService.deleteChallenge(id);
            return ResponseEntity.ok("Đã xóa thử thách thành công.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // challenges/{challengeId}/join
    @PostMapping("/{challengeId}/join")
    public ResponseEntity<?> joinChallenge(
            @PathVariable Long challengeId,
            @RequestParam("userId") Long userId) {
        try {
            challengeService.joinChallenge(challengeId, userId);
            return ResponseEntity.ok("Người dùng đã tham gia thử thách thành công.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // challenges/{challengeId}/leave
    @DeleteMapping("/{challengeId}/leave")
    public ResponseEntity<?> leaveChallenge(
            @PathVariable Long challengeId,
            @RequestParam("userId") Long userId) {
        try {
            challengeService.leaveChallenge(challengeId, userId);
            return ResponseEntity.ok("Người dùng đã rời thử thách thành công.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // challenges/{challengeId}/participants
    @GetMapping("/{challengeId}/participants")
    public ResponseEntity<List<ChallengeParticipantResponse>> getParticipantsByChallengeId(@PathVariable Long challengeId) {
        try {
            List<ChallengeParticipant> participants = challengeService.getParticipantsByChallengeId(challengeId);
            List<ChallengeParticipantResponse> participantResponses = participants.stream()
                    .map(ChallengeParticipantResponse::fromChallengeParticipant)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(participantResponses);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
