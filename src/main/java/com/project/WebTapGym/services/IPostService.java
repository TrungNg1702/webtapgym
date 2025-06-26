package com.project.WebTapGym.services;

import com.project.WebTapGym.models.Post;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IPostService {
    Post createPost(Long userId, String content, List<MultipartFile> files);
    List<Post> getAllPosts();
    Post getPostById(Long id);
    Post updatePost(Long postId, String content, List<MultipartFile> files);
    void deletePostImage(Long imageId);
    void deletePost(Long id);
}
