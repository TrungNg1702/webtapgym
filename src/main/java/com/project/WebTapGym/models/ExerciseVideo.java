package com.project.WebTapGym.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "exercise_videos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExerciseVideo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // Thêm fetch = FetchType.LAZY để tránh N+1 query nếu không cần thiết
    @JoinColumn(name = "exercise_id")
    @JsonIgnore
    private Exercise exercise;

    @Column(name = "video_url", length = 300)
    private String videoUrl;
}