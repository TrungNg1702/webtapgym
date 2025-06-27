package com.project.WebTapGym.responses; // Đặt trong gói DTO của bạn

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors; // Thêm import này

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChallengeResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // Có thể thêm số lượng người tham gia hoặc danh sách người tham gia rút gọn
    private List<ChallengeParticipantResponse> participants; // Danh sách người tham gia rút gọn

    // Phương thức tĩnh để chuyển đổi từ Challenge entity sang ChallengeResponse DTO
    public static ChallengeResponse fromChallenge(com.project.WebTapGym.models.Challenge challenge) {
        List<ChallengeParticipantResponse> participantResponses = null;
        if (challenge.getParticipants() != null) {
            participantResponses = challenge.getParticipants().stream()
                    .map(ChallengeParticipantResponse::fromChallengeParticipant)
                    .collect(Collectors.toList());
        }

        return ChallengeResponse.builder()
                .id(challenge.getId())
                .title(challenge.getTitle())
                .description(challenge.getDescription())
                .startDate(challenge.getStartDate())
                .endDate(challenge.getEndDate())
                .createdAt(challenge.getCreatedAt())
                .updatedAt(challenge.getUpdatedAt())
                .participants(participantResponses)
                .build();
    }
}
