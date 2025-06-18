package com.project.WebTapGym.services;

import com.project.WebTapGym.dtos.WorkoutStatusDTO;

public interface IWorkoutScheduleStatusService {
    void markWorkoutCompleted(Long userId, WorkoutStatusDTO dto);
}
