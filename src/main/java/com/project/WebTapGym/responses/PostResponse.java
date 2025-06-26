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
    private Long userId; // Chỉ trả về ID của người dùng
    private List<PostImageResponse> postImages; // Bao gồm ID và URL của ảnh
    private List<CommentResponse> comments; // Thêm danh sách bình luận
    private List<LikeResponse> likes;     // Thêm danh sách lượt thích
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

        List<CommentResponse> commentResponses = null;
        // Kiểm tra null để tránh LazyInitializationException nếu comments chưa được tải
        if (post.getComments() != null) {
            commentResponses = post.getComments().stream()
                    .map(CommentResponse::fromComment)
                    .collect(Collectors.toList());
        }

        List<LikeResponse> likeResponses = null;
        // Kiểm tra null để tránh LazyInitializationException nếu likes chưa được tải
        if (post.getLikes() != null) {
            likeResponses = post.getLikes().stream()
                    .map(LikeResponse::fromLike)
                    .collect(Collectors.toList());
        }

        return PostResponse.builder()
                .id(post.getId())
                .content(post.getContent())
                .userId(post.getUser() != null ? post.getUser().getId() : null)
                .postImages(images)
                .comments(commentResponses) // Gán danh sách bình luận đã chuyển đổi
                .likes(likeResponses)       // Gán danh sách lượt thích đã chuyển đổi
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
