package com.project.WebTapGym.repositories;

import com.project.WebTapGym.models.Exercise;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, Long>
{
    boolean existsByName(String name);

    Page<Exercise> findAll (Pageable pageable); // phan trang

    List<Exercise> findByMuscleMainGroupId(Long muscleMainGroupId);
}