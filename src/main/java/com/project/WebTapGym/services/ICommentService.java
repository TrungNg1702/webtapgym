package com.project.WebTapGym.services;

import com.project.WebTapGym.models.Comment;

import java.util.List;

public interface ICommentService {
    Comment createComment(Long postId, Long userId, String content);
    List<Comment> getAllCommentsByPostId(Long postId);
    void deleteComment(Long id);
}
