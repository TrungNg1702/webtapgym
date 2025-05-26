package com.project.WebTapGym;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class WebTapGymApplication implements WebMvcConfigurer { // Triển khai WebMvcConfigurer

	@Value("${upload.dir:uploads}") // Đọc giá trị từ application.properties hoặc dùng "uploads" mặc định
	private String uploadDir;

	public static void main(String[] args) {
		SpringApplication.run(WebTapGymApplication.class, args);
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// Đăng ký resource handler cho thư mục video bài tập
		registry.addResourceHandler("/exercise_videos/**") // URL pattern mà client sẽ truy cập
				.addResourceLocations("file:" + uploadDir + "/exercise_videos/"); // Đường dẫn thực tế trên hệ thống file
	}
}