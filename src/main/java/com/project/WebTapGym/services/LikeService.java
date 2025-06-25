package com.project.WebTapGym.services;

import com.project.WebTapGym.models.Like;
import com.project.WebTapGym.models.Post;
import com.project.WebTapGym.models.User;
import com.project.WebTapGym.repositories.LikeRepository;
import com.project.WebTapGym.repositories.PostRepository;
import com.project.WebTapGym.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService implements ILikeService{

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;


    @Override
    public void likePost(Long userId, Long postId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User không tồn tại"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post không tồn tại"));
        Like like = Like.builder()
                .user(user)
                .post(post)
                .build();
        likeRepository.save(like);
    }

    @Override
    public long countLikesByPostId(Long postId) {
        return likeRepository.countByPostId(postId);
    }

    @Override
    public void unlikePost(Long userId, Long postId) {
        Like like = likeRepository.findByPostIdAndUserId(postId, userId)
                .orElseThrow(() -> new RuntimeException("Chưa like để có thể unlike"));
        likeRepository.delete(like);
    }
}
