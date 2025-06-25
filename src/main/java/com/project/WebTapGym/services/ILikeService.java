package com.project.WebTapGym.services;

public interface ILikeService {
    void likePost(Long userId, Long postId);
    long countLikesByPostId(Long postId);
    void unlikePost(Long userId, Long postId);
}
