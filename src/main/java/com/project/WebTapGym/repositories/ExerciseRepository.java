package com.project.WebTapGym.repositories;

import com.project.WebTapGym.models.Exercise;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;



public interface ExerciseRepository extends JpaRepository<Exercise, Long>
{
    boolean existsByName(String name);

    Page<Exercise> findAll (Pageable pageable); // phan trang
}
