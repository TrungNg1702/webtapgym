package com.project.WebTapGym.services;

import com.project.WebTapGym.dtos.WorkoutScheduleDTO;
import com.project.WebTapGym.models.User;
import com.project.WebTapGym.models.WorkoutSchedule;
import com.project.WebTapGym.repositories.UserRepository;
import com.project.WebTapGym.repositories.WorkoutScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@RequiredArgsConstructor
public class WorkoutScheduleService implements IWorkoutScheduleService
{
    private final WorkoutScheduleRepository workoutScheduleRepository;
    private final UserRepository userRepository;
    @Override
    @Transactional
    public WorkoutSchedule createWorkoutSchedule(WorkoutScheduleDTO workoutScheduleDTO) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        WorkoutSchedule workoutSchedule = WorkoutSchedule.builder()
                .userId(currentUser)
                .dayOfWeek(workoutScheduleDTO.getDayOfWeek())
                .timeSlot(workoutScheduleDTO.getTimeSlot())
                .workoutType(workoutScheduleDTO.getWorkoutType())
                .duration(workoutScheduleDTO.getDuration())
                .build();
        return workoutScheduleRepository.save(workoutSchedule);
    }

    @Override
    @Transactional
    public List<WorkoutSchedule> getWorkoutSchedulesByUserId(Long userId) {
        return workoutScheduleRepository.findByUserId_Id(userId);
    }

    @Override
    public void deleteWorkoutSchedule(Long scheduleId) {
        WorkoutSchedule workoutSchedule = workoutScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Workout schedule not found"));
        workoutScheduleRepository.delete(workoutSchedule);
    }

    @Override
    public List<WorkoutSchedule> suggestWorkoutSchedule(Long userId, String goal) {
        return List.of();
    }
}
