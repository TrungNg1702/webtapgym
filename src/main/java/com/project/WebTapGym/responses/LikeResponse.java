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
public class LikeResponse {
    private Long id;
    private Long postId; // Chỉ ID của bài đăng
    private Long userId; // Chỉ ID của người dùng
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Phương thức tĩnh để chuyển đổi từ Like entity sang LikeResponse DTO
    public static LikeResponse fromLike(com.project.WebTapGym.models.Like like) {
        return LikeResponse.builder()
                .id(like.getId())
                .postId(like.getPost() != null ? like.getPost().getId() : null)
                .userId(like.getUser() != null ? like.getUser().getId() : null)
                .createdAt(like.getCreatedAt())
                .updatedAt(like.getUpdatedAt())
                .build();
    }
}
