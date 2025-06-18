package com.project.WebTapGym.controllers;

import com.project.WebTapGym.dtos.HealthChatRequest;
import com.project.WebTapGym.dtos.HealthRecordDTO;
import com.project.WebTapGym.responses.HealthRecordResponse;
import com.project.WebTapGym.services.IHealthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/health-chat")
@RequiredArgsConstructor
public class HealthChatController {
    private final IHealthService healthService;

    @PostMapping("/chat/{userId}")
    public ResponseEntity<Map<String, Object>> healthChat(
            @PathVariable Long userId,
            @RequestBody HealthChatRequest request
    ) {
        Map<String, Object> result = healthService.healthChat(userId, request.getBodyIndex(), request.getGoal());
        if (result.containsKey("error")) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/add/{userId}")
    public ResponseEntity<?> addRecord(@PathVariable Long userId, @RequestBody HealthRecordDTO request) {
        try {
            healthService.saveRecord(userId, request);
            return ResponseEntity.ok(Map.of("message", "Lưu thành công"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<HealthRecordResponse>> getHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(healthService.getRecordHistory(userId));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getAdvice(@PathVariable Long userId) {
        Map<String, Object> result = healthService.generateHealthAdvice(userId);
        if (result.containsKey("error")) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }
}
