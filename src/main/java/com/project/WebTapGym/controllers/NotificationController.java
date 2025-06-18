package com.project.WebTapGym.controllers;

import com.project.WebTapGym.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final EmailService emailService;

    @PostMapping("/send-reminder")
    public ResponseEntity<?> sendReminderEmail(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String content
    ) {
        try {
            emailService.sendReminderEmail(to, subject, content);
            return ResponseEntity.ok("Email đã gửi đến: " + to);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi gửi email: " + e.getMessage());
        }
    }
}
