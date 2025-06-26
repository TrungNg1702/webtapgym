package com.project.WebTapGym.controllers;

import com.project.WebTapGym.responses.LikeResponse; // Import LikeResponse DTO
import com.project.WebTapGym.models.Like; // Vẫn cần entity để làm việc với service
import com.project.WebTapGym.services.ILikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus; // Thêm import này
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/likes") // Đặt đường dẫn base cho Likes
public class LikeController {

    private final ILikeService likeService;

    // Thích một bài đăng
    @PostMapping("/like")
    public ResponseEntity<?> likePost(
            @RequestParam("userId") Long userId,
            @RequestParam("postId") Long postId) {
        try {
            Like like = likeService.likePost(userId, postId);
            // Chuyển đổi Like entity sang LikeResponse DTO trước khi trả về
            return ResponseEntity.status(HttpStatus.CREATED).body(LikeResponse.fromLike(like));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    // Bỏ thích một bài đăng
    @DeleteMapping("/unlike")
    public ResponseEntity<?> unlikePost(
            @RequestParam("userId") Long userId,
            @RequestParam("postId") Long postId) {
        try {
            likeService.unlikePost(userId, postId);
            return ResponseEntity.ok("Đã bỏ thích bài đăng thành công.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    // Đếm số lượt thích của một bài đăng
    @GetMapping("/count/{postId}")
    public ResponseEntity<Long> countLikesByPostId(@PathVariable Long postId) {
        try {
            long likeCount = likeService.countLikesByPostId(postId);
            return ResponseEntity.ok(likeCount);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(0L); // Trả về 0 hoặc lỗi tùy thuộc vào yêu cầu
        }
    }


    // Kiểm tra xem một người dùng cụ thể đã thích một bài đăng cụ thể hay chưa
    @GetMapping("/hasLiked")
    public ResponseEntity<Boolean> hasUserLikedPost(
            @RequestParam("userId") Long userId,
            @RequestParam("postId") Long postId) {
        try {
            boolean hasLiked = likeService.hasUserLikedPost(userId, postId);
            return ResponseEntity.ok(hasLiked);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(false); // Xử lý lỗi nếu cần
        }
    }
}
