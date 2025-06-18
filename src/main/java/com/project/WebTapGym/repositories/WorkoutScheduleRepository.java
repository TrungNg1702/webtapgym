package com.project.WebTapGym.repositories;

import com.project.WebTapGym.models.WorkoutSchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WorkoutScheduleRepository extends JpaRepository<WorkoutSchedule, Long> {
    Page<WorkoutSchedule> findByUserId_Id(Long userId, Pageable pageable);
    @Query("SELECT ws FROM WorkoutSchedule ws WHERE ws.dayOfWeek IN :days")
    List<WorkoutSchedule> findSchedulesForToday(@Param("today") String today);

}
