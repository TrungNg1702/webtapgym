package com.project.WebTapGym.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {
    private Long id;
    private String content;
    private Long postId;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public static CommentResponse fromComment(com.project.WebTapGym.models.Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .postId(comment.getPost() != null ? comment.getPost().getId() : null)
                .userId(comment.getUser() != null ? comment.getUser().getId() : null)
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
