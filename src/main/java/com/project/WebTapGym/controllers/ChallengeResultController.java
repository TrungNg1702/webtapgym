package com.project.WebTapGym.controllers;

import com.project.WebTapGym.models.ChallengeResult;
import com.project.WebTapGym.services.ChallengeResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/challenge-results")
@RequiredArgsConstructor
public class ChallengeResultController {

    private final ChallengeResultService challengeResultService;

    @PostMapping("/complete")
    public ResponseEntity<?> completeChallenge(
            @RequestParam Long userID,
            @RequestParam Long challengeID
    ) {
        challengeResultService.markCompleted( userID, challengeID );
        return ResponseEntity.ok("Challenge completed successfully.");
    }

    @PostMapping("/progress")
    public ResponseEntity<?> updateProgress(
            @RequestParam Long userId,
            @RequestParam Long challengeId,
            @RequestParam int progress
    ) {
        challengeResultService.updateProgress(userId, challengeId, progress);
        return ResponseEntity.ok("Cập nhật tiến độ thành công!");
    }

    @GetMapping("/status")
    public ResponseEntity<ChallengeResult> getResult(
            @RequestParam Long userId,
            @RequestParam Long challengeId
    ) {
        return ResponseEntity.ok(challengeResultService.getResult(userId, challengeId));
    }
}
