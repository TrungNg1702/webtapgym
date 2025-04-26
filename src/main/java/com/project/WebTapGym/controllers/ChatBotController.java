package com.project.WebTapGym.controllers;


import com.project.WebTapGym.services.ChatGPTService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @PostMapping("")
    public ResponseEntity<?> chatWithBot(
            @RequestBody Map<String, String> request){

        try{
            String message = request.get("message");
            String reply = chatGPTService.askChatGPT(message);
            return ResponseEntity.ok(Map.of("reply", reply));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
}
