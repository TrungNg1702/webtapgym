package com.project.WebTapGym.repositories;

import com.project.WebTapGym.models.MuscleGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MuscleGroupsRepository extends JpaRepository<MuscleGroup, Long>
{
    List<MuscleGroup> findByMuscleMainGroupId(Long muscleMainGroupId);

}
