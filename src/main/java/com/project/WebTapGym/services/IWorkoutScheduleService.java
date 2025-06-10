package com.project.WebTapGym.services;

import com.project.WebTapGym.dtos.WorkoutScheduleDTO;
import com.project.WebTapGym.models.WorkoutSchedule;

import java.util.List;

public interface IWorkoutScheduleService
{

    WorkoutSchedule createWorkoutSchedule(WorkoutScheduleDTO workoutScheduleDTO);

    List<WorkoutSchedule> getWorkoutSchedulesByUserId(Long userId);


    void deleteWorkoutSchedule(Long scheduleId);

    List<WorkoutSchedule> suggestWorkoutSchedule(Long userId, String goal);
}

