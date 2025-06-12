package com.project.WebTapGym.services;

import com.project.WebTapGym.dtos.WorkoutScheduleDTO;
import com.project.WebTapGym.models.WorkoutSchedule;
import com.project.WebTapGym.responses.WorkoutScheduleChatResponse;
import com.project.WebTapGym.responses.WorkoutScheduleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface IWorkoutScheduleService
{

    WorkoutSchedule createWorkoutSchedule(WorkoutScheduleDTO workoutScheduleDTO);

    Page<WorkoutScheduleResponse> getWorkoutSchedulesByUserId(Long userId, PageRequest pageRequest);


    void deleteWorkoutSchedule(Long scheduleId);

    WorkoutScheduleChatResponse suggestWorkoutSchedule(Long userId, String goal);
}

