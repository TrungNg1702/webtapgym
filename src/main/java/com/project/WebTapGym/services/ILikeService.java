package com.project.WebTapGym.services;

import com.project.WebTapGym.models.Like;

public interface ILikeService {
    // Thay đổi kiểu trả về sang Like entity để Controller xử lý DTO
    Like likePost(Long userId, Long postId);
    long countLikesByPostId(Long postId);
    void unlikePost(Long userId, Long postId);
    // Để kiểm tra xem người dùng đã like một bài đăng cụ thể hay chưa
    boolean hasUserLikedPost(Long userId, Long postId);
}
