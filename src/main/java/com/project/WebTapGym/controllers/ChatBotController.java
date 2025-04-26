package com.project.WebTapGym.controllers;


import com.project.WebTapGym.services.ChatGPTService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/chatbot")
@RequiredArgsConstructor
public class ChatBotController {
    private final ChatGPTService chatGPTService;

    @PostMapping
    public ResponseEntity<String> handleMessage(@RequestBody String userMessage) {
        try {
            if (userMessage == null || userMessage.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Tin nhắn không được để trống.");
            }
            String response = chatGPTService.sendMessageToChatbot(userMessage);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi server: " + e.getMessage());
        }
    }
}