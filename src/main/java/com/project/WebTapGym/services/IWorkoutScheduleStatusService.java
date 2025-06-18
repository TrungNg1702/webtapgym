package com.project.WebTapGym.services;

import com.project.WebTapGym.dtos.WorkoutStatisticsResponseDTO;
import com.project.WebTapGym.dtos.WorkoutStatusDTO;

public interface IWorkoutScheduleStatusService {
    void markWorkoutCompleted(Long userId, WorkoutStatusDTO dto);

    WorkoutStatisticsResponseDTO getWorkoutStatistics(Long userId);

}
