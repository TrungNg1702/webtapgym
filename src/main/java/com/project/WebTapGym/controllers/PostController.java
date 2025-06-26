package com.project.WebTapGym.controllers;

import com.project.WebTapGym.models.Post;
import com.project.WebTapGym.responses.PostResponse;
import com.project.WebTapGym.services.IPostService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/post")
public class PostController {

    private final IPostService postService;

    @PostMapping("/create")
    public ResponseEntity<?> createPost(
            @RequestParam("userId") Long userId,
            @RequestParam("content") String content,
            @RequestParam(value = "files", required = false) List<MultipartFile> files) {
        try {
            Post post = postService.createPost(userId, content, files);
            return ResponseEntity.ok(PostResponse.fromPost(post));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating post: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> getAll() {
        List<Post> posts = postService.getAllPosts();
        // Chuyển đổi danh sách Post entity sang danh sách PostResponse
        List<PostResponse> postResponses = posts.stream()
                .map(PostResponse::fromPost)
                .collect(Collectors.toList());
        return ResponseEntity.ok(postResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long id) {
        try {
            Post post = postService.getPostById(id);
            return ResponseEntity.ok(PostResponse.fromPost(post));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updatePost(
            @PathVariable Long id,
            @RequestParam("content") String content,
            @RequestParam(value = "files", required = false) List<MultipartFile> files) {
        try {
            Post updatedPost = postService.updatePost(id, content, files);
            return ResponseEntity.ok(PostResponse.fromPost(updatedPost));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating post: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.ok("Post deleted successfully.");
    }

    @DeleteMapping("/deleteImage/{id}")
    public ResponseEntity<?> deleteImagePost(@PathVariable Long id) {
        postService.deletePostImage(id);
        return ResponseEntity.ok("Image deleted successfully.");
    }
}
