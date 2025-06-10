package com.project.WebTapGym.repositories;

import com.project.WebTapGym.models.WorkoutSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkoutScheduleRepository extends JpaRepository<WorkoutSchedule, Long> {
    List<WorkoutSchedule> findByUserId_Id(Long userId);
}
