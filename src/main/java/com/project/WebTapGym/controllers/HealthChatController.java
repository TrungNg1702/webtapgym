package com.project.WebTapGym.controllers;

import com.project.WebTapGym.dtos.HealthChatRequest;
import com.project.WebTapGym.services.IHealthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
