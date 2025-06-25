package com.project.WebTapGym.services;

import com.project.WebTapGym.models.Post;
import com.project.WebTapGym.models.User;
import com.project.WebTapGym.repositories.PostRepository;
import com.project.WebTapGym.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class PostService implements IPostService{

    private final UserRepository userRepository;
    private final PostRepository postRepository;


    @Override
    public Post createPost(Long userId, String content, String imageUrl) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = Post.builder()
                .user(existingUser)
                .content(content)
                .imageUrl(imageUrl)
                .build();

        return postRepository.save(post);
    }

    @Override
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @Override
    public void deletePost(Long id) {
        Post existingPost = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        postRepository.delete(existingPost);
    }
}
