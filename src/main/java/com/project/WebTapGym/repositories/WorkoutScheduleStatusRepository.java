package com.project.WebTapGym.repositories;

import com.project.WebTapGym.models.WorkoutScheduleStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkoutScheduleStatusRepository extends JpaRepository<WorkoutScheduleStatus, Long> {
    Optional<WorkoutScheduleStatus> findBySchedule_IdAndUser_Id(Long scheduleId, Long userId);
    List<WorkoutScheduleStatus> findByUser_IdAndCompletedTrue(Long user_id);

    List<WorkoutScheduleStatus> findByUser_Id(Long userId);
    long countByUser_Id(Long userId);
    long countByUser_IdAndCompletedTrue(Long userId);
}
