package com.project.WebTapGym.services;

import com.project.WebTapGym.models.Like;
import com.project.WebTapGym.models.Post;
import com.project.WebTapGym.models.User;
import com.project.WebTapGym.repositories.LikeRepository;
import com.project.WebTapGym.repositories.PostRepository;
import com.project.WebTapGym.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional; // Thêm import này

import java.time.LocalDateTime; // Thêm import này

@Service
@RequiredArgsConstructor
public class LikeService implements ILikeService{

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Override
    @Transactional // Đảm bảo phương thức này là transactional
    public Like likePost(Long userId, Long postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post không tồn tại"));

        // Kiểm tra xem user đã like post này chưa để tránh trùng lặp
        if (likeRepository.findByPostIdAndUserId(postId, userId).isPresent()) {
            throw new RuntimeException("Người dùng đã thích bài đăng này rồi.");
        }

        Like like = Like.builder()
                .user(user)
                .post(post)
                .build();
        return likeRepository.save(like);
    }

    @Override
    public long countLikesByPostId(Long postId) {
        return likeRepository.countByPostId(postId);
    }

    @Override
    @Transactional
    public void unlikePost(Long userId, Long postId) {
        Like like = likeRepository.findByPostIdAndUserId(postId, userId)
                .orElseThrow(() -> new RuntimeException("Người dùng chưa thích bài đăng này để có thể bỏ thích."));
        likeRepository.delete(like);
    }

    @Override
    public boolean hasUserLikedPost(Long userId, Long postId) {
        return likeRepository.findByPostIdAndUserId(postId, userId).isPresent();
    }
}
