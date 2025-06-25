package com.project.WebTapGym.services;

import com.project.WebTapGym.models.Post;

import java.util.List;

public interface IPostService {
    Post createPost(Long userId, String content, String imageUrl);
    List<Post> getAllPosts();
    void deletePost(Long id);
}
