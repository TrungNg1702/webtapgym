package com.project.WebTapGym.models;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore; // Thêm import này

@Entity
@Table(name = "post_images")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    @JsonIgnore // Thêm chú thích này để tránh vòng lặp vô hạn trong JSON
    private Post post;
}
