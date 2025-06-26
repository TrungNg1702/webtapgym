package com.project.WebTapGym.controllers;

import com.project.WebTapGym.responses.CommentResponse;
import com.project.WebTapGym.models.Comment;
import com.project.WebTapGym.services.ICommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/comments")
public class CommentController {

    private final ICommentService commentService;

    @PostMapping("")
    public ResponseEntity<?> createComment(
            @RequestParam("postId") Long postId,
            @RequestParam("userId") Long userId,
            @RequestParam("content") String content) {
        try {
            Comment comment = commentService.createComment(postId, userId, content);
            // Chuyển đổi Comment entity sang CommentResponse DTO trước khi trả về
            return ResponseEntity.ok(CommentResponse.fromComment(comment));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentResponse>> getAllCommentsByPostId(@PathVariable Long postId) {
        List<Comment> comments = commentService.getAllCommentsByPostId(postId);
        // Chuyển đổi danh sách Comment entity sang danh sách CommentResponse DTO
        List<CommentResponse> commentResponses = comments.stream()
                .map(CommentResponse::fromComment)
                .collect(Collectors.toList());
        return ResponseEntity.ok(commentResponses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateComment(@PathVariable Long id, @RequestParam("content") String content)
    {
        try
        {
            Comment updatedComment = commentService.updateComment(id, content);
            return ResponseEntity.ok(CommentResponse.fromComment(updatedComment));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
        try {
            commentService.deleteComment(id);
            return ResponseEntity.ok("Đã xóa bình luận thành công.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
