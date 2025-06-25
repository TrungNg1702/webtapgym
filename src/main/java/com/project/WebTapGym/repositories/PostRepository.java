package com.project.WebTapGym.repositories;

import com.project.WebTapGym.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUserId(Long userId);
    List<Post> findByChallengeId(Long challengeId);
}
