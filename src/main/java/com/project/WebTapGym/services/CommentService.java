package com.project.WebTapGym.services;

import com.project.WebTapGym.models.Comment;
import com.project.WebTapGym.models.Post;
import com.project.WebTapGym.models.User;
import com.project.WebTapGym.repositories.CommentRepository;
import com.project.WebTapGym.repositories.PostRepository;
import com.project.WebTapGym.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService implements ICommentService{

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;



    @Override
    public Comment createComment(Long postId, Long userId, String content) {
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = Comment.builder()
                .post(existingPost)
                .user(existingUser)
                .content(content)
                .build();

        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> getAllCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    @Override
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }
}
