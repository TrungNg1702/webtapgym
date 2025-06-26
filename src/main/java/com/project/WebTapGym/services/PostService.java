package com.project.WebTapGym.services;

import com.project.WebTapGym.models.Post;
import com.project.WebTapGym.models.PostImage;
import com.project.WebTapGym.models.User;
import com.project.WebTapGym.repositories.PostImageRepository;
import com.project.WebTapGym.repositories.PostRepository;
import com.project.WebTapGym.repositories.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value; // Thêm import Value
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files; // Thêm import Files
import java.nio.file.Path; // Thêm import Path
import java.nio.file.Paths; // Thêm import Paths
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;

    @Value("${upload.dir}") // Inject thư mục upload từ application.properties
    private String uploadDir;

    @Override
    @Transactional
    public Post createPost(Long userId, String content, List<MultipartFile> files) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        Post post = Post.builder()
                .user(existingUser)
                .content(content)
                .build();

        if (files != null && !files.isEmpty()) {
            List<PostImage> postImages = new ArrayList<>();

            // Tạo thư mục upload nếu nó chưa tồn tại
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                throw new RuntimeException("Không thể tạo thư mục upload: " + uploadPath, e);
            }

            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                    try {
                        // Giải quyết đường dẫn đầy đủ cho tệp đích
                        Path destinationFilePath = uploadPath.resolve(fileName);
                        file.transferTo(destinationFilePath);

                        PostImage postImage = new PostImage();
                        // Lưu đường dẫn tương đối hoặc URL có thể truy cập được cho hình ảnh
                        // Giả sử "/uploads/" được cấu hình để phục vụ các tệp tĩnh từ `uploadDir`
                        postImage.setImageUrl("/uploads/" + fileName);
                        postImage.setPost(post);
                        postImages.add(postImage);
                    } catch (IOException e) {
                        throw new RuntimeException("Lỗi khi lưu tệp hình ảnh: " + fileName, e);
                    }
                }
            }
            post.setPostImages(postImages);
        }

        return postRepository.save(post);
    }

    @Override
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @Override
    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài đăng"));
    }

    @Override
    @Transactional
    public Post updatePost(Long postId, String content, List<MultipartFile> files) {
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài đăng"));
        existingPost.setContent(content);

        // Kiểm tra nếu có tệp mới để thêm vào
        if (files != null && !files.isEmpty()) {
            // Đảm bảo danh sách postImages của bài đăng hiện tại không null
            if (existingPost.getPostImages() == null) {
                existingPost.setPostImages(new ArrayList<>());
            }

            // Tạo thư mục upload nếu nó chưa tồn tại
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                throw new RuntimeException("Không thể tạo thư mục upload để cập nhật: " + uploadPath, e);
            }

            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                    try {
                        Path destinationFilePath = uploadPath.resolve(fileName);
                        file.transferTo(destinationFilePath);

                        PostImage postImage = new PostImage();
                        postImage.setImageUrl("/uploads/" + fileName); // Giả sử "/uploads/" ánh xạ tới uploadDir
                        postImage.setPost(existingPost); // Thiết lập mối quan hệ với bài đăng hiện tại
                        existingPost.getPostImages().add(postImage); // Thêm ảnh mới vào danh sách hiện có
                    } catch (IOException e) {
                        throw new RuntimeException("Lỗi khi lưu tệp hình ảnh mới để cập nhật: " + fileName, e);
                    }
                }
            }
        }

        existingPost.setUpdatedAt(LocalDateTime.now()); // Cập nhật thời gian updatedAt
        return postRepository.save(existingPost); // Lưu bài đăng để cập nhật các mối quan hệ PostImage
    }

    @Override
    public void deletePostImage(Long imageId) {
        PostImage existingPostImage = postImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Khong tim thay anh"));
        postImageRepository.delete(existingPostImage);
    }

    @Override
    public void deletePost(Long id) {
        Post existingPost = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài đăng"));

        // Tùy chọn, xóa các tệp hình ảnh vật lý liên quan đến bài đăng
        if (existingPost.getPostImages() != null) {
            for (PostImage image : existingPost.getPostImages()) {
                // Xây dựng đường dẫn đầy đủ đến tệp hình ảnh
                // Đảm bảo `uploadDir` được cấu hình chính xác và trỏ đến thư mục vật lý
                // và `image.getImageUrl()` lưu đường dẫn như "/uploads/filename.jpg"
                String imageFileName = Paths.get(image.getImageUrl()).getFileName().toString();
                Path filePathToDelete = Paths.get(uploadDir).resolve(imageFileName).toAbsolutePath().normalize();
                try {
                    Files.deleteIfExists(filePathToDelete);
                } catch (IOException e) {
                    System.err.println("Không thể xóa tệp hình ảnh: " + filePathToDelete + " trong quá trình xóa bài đăng. " + e.getMessage());
                }
            }
        }
        postRepository.delete(existingPost);
    }
}
