package com.project.WebTapGym.services;

import com.project.WebTapGym.models.Comment;
import com.project.WebTapGym.models.Post;
import com.project.WebTapGym.models.User;
import com.project.WebTapGym.repositories.CommentRepository;
import com.project.WebTapGym.repositories.PostRepository;
import com.project.WebTapGym.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime; // Thêm import này
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService implements ICommentService{

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;


    @Override
    @Transactional // Đảm bảo phương thức này là transactional
    public Comment createComment(Long postId, Long userId, String content) {
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài đăng"));

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        Comment comment = Comment.builder()
                .post(existingPost)
                .user(existingUser)
                .content(content)
                .build();

        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> getAllCommentsByPostId(Long postId) {
        // Đảm bảo rằng bài đăng tồn tại nếu bạn muốn ném ngoại lệ khi postId không hợp lệ
        // Post existingPost = postRepository.findById(postId)
        //         .orElseThrow(() -> new RuntimeException("Không tìm thấy bài đăng để lấy bình luận."));
        return commentRepository.findByPostId(postId);
    }

    @Override
    public Comment updateComment(Long commentId, String content) {
        Comment existingComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Khong tim thay binh luan"));

        existingComment.setContent(content);
        existingComment.setUpdatedAt(LocalDateTime.now());
        return commentRepository.save(existingComment);
    }

    @Override
    @Transactional
    public void deleteComment(Long id) {
        // Kiểm tra xem bình luận có tồn tại không trước khi xóa
        if (!commentRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy bình luận để xóa.");
        }
        commentRepository.deleteById(id);
    }
}
