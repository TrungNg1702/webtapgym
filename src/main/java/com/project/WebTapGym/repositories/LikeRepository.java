package com.project.WebTapGym.repositories;

import com.project.WebTapGym.models.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByUserIdAndPostId(Long userId, Long postId);
    long countByPostId(Long postId);
    Optional<Like> findByPostIdAndUserId(Long postId, Long userId);
}
