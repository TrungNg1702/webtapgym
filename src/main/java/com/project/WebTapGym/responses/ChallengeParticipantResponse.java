package com.project.WebTapGym.responses; // Đặt trong gói DTO của bạn

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChallengeParticipantResponse {
    private Long id;
    private Long challengeId;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Phương thức tĩnh để chuyển đổi từ ChallengeParticipant entity sang ChallengeParticipantResponse DTO
    public static ChallengeParticipantResponse fromChallengeParticipant(com.project.WebTapGym.models.ChallengeParticipant participant) {
        return ChallengeParticipantResponse.builder()
                .id(participant.getId())
                .challengeId(participant.getChallenge() != null ? participant.getChallenge().getId() : null)
                .userId(participant.getUser() != null ? participant.getUser().getId() : null)
                .createdAt(participant.getCreatedAt())
                .updatedAt(participant.getUpdatedAt())
                .build();
    }
}
