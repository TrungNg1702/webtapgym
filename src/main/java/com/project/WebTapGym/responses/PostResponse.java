package com.project.WebTapGym.responses;

import com.project.WebTapGym.models.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponse {
    private Long id;
    private String content;
    private Long userId;
    // Thay đổi từ List<String> sang List<PostImageResponse>
    private List<PostImageResponse> postImages;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Định nghĩa DTO lồng nhau cho thông tin hình ảnh
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PostImageResponse {
        private Long id;
        private String imageUrl;
    }

    // Phương thức tĩnh để chuyển đổi từ Post entity sang PostResponse DTO
    public static PostResponse fromPost(com.project.WebTapGym.models.Post post) {
        List<PostImageResponse> images = null;
        if (post.getPostImages() != null) {
            images = post.getPostImages().stream()
                    .map(postImage -> PostImageResponse.builder()
                            .id(postImage.getId())
                            .imageUrl(postImage.getImageUrl())
                            .build())
                    .collect(Collectors.toList());
        }

        return PostResponse.builder()
                .id(post.getId())
                .content(post.getContent())
                .userId(post.getUser() != null ? post.getUser().getId() : null)
                .postImages(images) // Gán danh sách PostImageResponse
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
