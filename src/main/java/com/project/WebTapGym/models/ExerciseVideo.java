package com.project.WebTapGym.models;

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

    @ManyToOne
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

    @Column(name = "video_url", length = 300)
    private String videoUrl;
}
