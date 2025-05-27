package com.project.WebTapGym.repositories;

import com.project.WebTapGym.models.ExerciseVideo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExerciseVideoRepository extends JpaRepository<ExerciseVideo, Long> {
    List<ExerciseVideo> findByExerciseId(Long exerciseId);
}